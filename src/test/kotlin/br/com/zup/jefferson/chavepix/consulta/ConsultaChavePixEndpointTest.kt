package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.ConsultaDadosChavePixServiceGrpc
import br.com.zup.jefferson.ConsultaPixRequest
import br.com.zup.jefferson.chavepix.*
import br.com.zup.jefferson.enums.*
import br.com.zup.jefferson.sistemaexterno.BankAccount
import br.com.zup.jefferson.sistemaexterno.BcbClient
import br.com.zup.jefferson.sistemaexterno.Owner
import br.com.zup.jefferson.sistemaexterno.PixKeyDetailsResponse
import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)
internal class ConsultaChavePixEndpointTest(
    @Inject val repository: PixRepository,
    @Inject val grpcConsulta: ConsultaDadosChavePixServiceGrpc.ConsultaDadosChavePixServiceBlockingStub,
) {

    @Inject
    lateinit var bcbClient: BcbClient
    lateinit var pixKeyDetailsResponse: PixKeyDetailsResponse
    lateinit var chavePixResponse: ChavePixResponse

    @BeforeEach
    internal fun setUp() {
        val conta = Conta(
            instituicao = Instituicoes.nome("60701190"),
            agencia = "0001",
            numeroDaConta = "291900",
            nomeTitular = "Rafael M C Ponte",
            cpfTitular = "02467781054"
        )

       val chaveExistente = ChavePix(
            idCliente = UUID.randomUUID(),
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            tipoDeChave = TipoDeChave.CPF,
            chavePix = "02467781054",
            conta = conta)

        repository.save(chaveExistente)

        val bankAccount = BankAccount(
            participant = "60701190",
            branch = "0001",
            accountNumber = "291900",
            accountType = AccountType.CACC
        )

        val owner = Owner(
            type = OwnerType.NATURAL_PERSON,
            name = "Rafael M C Ponte",
            taxIdNumber = "02467781054"
        )

        pixKeyDetailsResponse = PixKeyDetailsResponse(
            keyType = KeyType.CPF,
            key = "07278778107",
            bankAccount = bankAccount,
            owner = owner,
            createdAt = LocalDateTime.now()
        )

        chavePixResponse = ChavePixResponse(
            pixId = chaveExistente.pixId,
            clienteId = chaveExistente.idCliente,
            tipoDeChave = TipoDeChave.CPF,
            chavePix = chaveExistente.chavePix!!,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = conta
        )
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun `deveria retornar dados de uma chave pix do banco do brasil quando ela existir`() {

        Mockito.`when`(bcbClient.buscaChavePixNoBcb(pixKeyDetailsResponse.key))
            .thenReturn(HttpResponse.ok(pixKeyDetailsResponse))

        val filtro = ConsultaPixRequest.FiltroPorPixId.newBuilder()
            .setClienteId("")
            .setPixId("")
            .build()

        val request = ConsultaPixRequest.newBuilder()
            .setPixId(filtro)
            .setChavePix(pixKeyDetailsResponse.key)
            .build()

        val response = grpcConsulta.consulta(request)

        assertEquals(pixKeyDetailsResponse.key, response.chave.chavePix)
        assertEquals(KeyType.CPF.name, response.chave.tipoDeChave.name)
        assertEquals("", response.clienteId)
        assertEquals("", response.pixId)
    }

    @Test
    fun `deveria retornar dados de uma chave pix do nosso sistema quando existir idcliente e pixid`() {
        Mockito.`when`(bcbClient.buscaChavePixNoBcb(pixKeyDetailsResponse.key))
            .thenReturn(HttpResponse.ok(pixKeyDetailsResponse))

        val filtro = ConsultaPixRequest.FiltroPorPixId.newBuilder()
            .setClienteId(chavePixResponse.clienteId.toString())
            .setPixId(chavePixResponse.pixId)
            .build()

        val request = ConsultaPixRequest.newBuilder()
            .setPixId(filtro)
            .build()

        val response = grpcConsulta.consulta(request)

        assertEquals(chavePixResponse.chavePix, response.chave.chavePix)
        assertEquals(TipoDeChave.CPF.name, response.chave.tipoDeChave.name)
        assertEquals(chavePixResponse.clienteId.toString(), response.clienteId)
        assertEquals(chavePixResponse.pixId, response.pixId)
    }

    @Test
    fun `deveria retornar status not found quando chavePix do banco do brasil nao existir`(){

        Mockito.`when`(bcbClient.buscaChavePixNoBcb(pixKeyDetailsResponse.key))
            .thenReturn(HttpResponse.created(pixKeyDetailsResponse))

        val request = ConsultaPixRequest.newBuilder()
            .setChavePix("07278778107")
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcConsulta.consulta(request)
        }
        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave pix não foi encontrada", error.status.description)
    }

    @Test
    fun `deveria retornar status not found quando nao existir idcliente e pixid no nosso sistema`(){

        val filtro = ConsultaPixRequest.FiltroPorPixId.newBuilder()
            .setClienteId(UUID.randomUUID().toString())
            .setPixId("89f5e2f8-c70d-4547-9e78-0b01f80893df")
            .build()

        val request = ConsultaPixRequest.newBuilder()
            .setPixId(filtro)
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcConsulta.consulta(request)
        }
        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave não encontrada ou não pertence ao requisitante", error.status.description)
    }

    @Test
    fun `deveria retornar invalid argument quando chavePix do banco do brasil for invalido`(){
        val error = assertThrows<StatusRuntimeException>(){
            grpcConsulta.consulta(ConsultaPixRequest.newBuilder()
                .setChavePix("")
                .build())
        }
        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
        assertEquals("Parametro Invalido", error.status.description)
    }

    @Test
    fun `deveria retornar invalid argument quando idcliene e pixid for invalido`(){
        val filtro = ConsultaPixRequest.FiltroPorPixId.newBuilder()
            .setClienteId("")
            .setPixId("")
            .build()

        val error = assertThrows<StatusRuntimeException>(){
            grpcConsulta.consulta(ConsultaPixRequest.newBuilder()

                .build())
        }
        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
        assertEquals("Parametro Invalido", error.status.description)
    }


    @Test
    fun `deveria retornar invalid argument quando nao for informado idcliente pixid e chavepix`(){

        val error = assertThrows<StatusRuntimeException>(){
            grpcConsulta.consulta(ConsultaPixRequest.newBuilder().build())
        }
        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
        assertEquals("Parametro Invalido", error.status.description)
    }


    @MockBean(BcbClient::class)
    fun bcbClienteMock(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }
}

@Factory
class ClientFactory {
    @Bean
    @Replaces
    fun stub(@GrpcChannel(GrpcServerChannel.NAME) channel: Channel): ConsultaDadosChavePixServiceGrpc.ConsultaDadosChavePixServiceBlockingStub {
        return ConsultaDadosChavePixServiceGrpc.newBlockingStub(channel)
    }
}