package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.PixServiceGrpc
import br.com.zup.jefferson.RegistraChavePixRequest
import br.com.zup.jefferson.enums.*
import br.com.zup.jefferson.sistemaexterno.*
import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class ChavePixEndpointTest(
    @Inject val repository: PixRepository,
    @Inject val grpcClient: PixServiceGrpc.PixServiceBlockingStub,
) {
    @field:Inject
    lateinit var bcbClient: BcbClient

    @field:Inject
    lateinit var itauClient: ItauClient

    lateinit var requestChavePix: RegistraChavePixRequest
    lateinit var contaResponse: ContaResponse

    lateinit var createPixKeyRequest: CreatePixKeyRequest
    lateinit var createPixKeyResponse: CreatePixKeyResponse


    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
        val instituicaoResponse = InstituicaoResponse(
            "ITAU UNIBANCO S.A",
            "60701190"
        )
        val titularResponse = TitularResponse(
            "Yuri Matheus",
            "43845574003"
        )
        contaResponse = ContaResponse(
            "CONTA_CORRENTE",
            instituicaoResponse,
            "0001",
            "965214",
            titularResponse)

        requestChavePix = RegistraChavePixRequest.newBuilder()
            .setChavePix("yuri@gmail.com")
            .setIdCliente(UUID.randomUUID().toString())
            .setTipoDeChave(br.com.zup.jefferson.TipoDeChave.EMAIL)
            .setTipoDeConta(br.com.zup.jefferson.TipoDeConta.CONTA_CORRENTE)
            .build()

        val owner = Owner(
            OwnerType.NATURAL_PERSON,
            name = "Yuri Matheus",
            taxIdNumber = "43845574003"
        )
        val bankAccount = BankAccount(
            participant = "60701190",
            branch = "0001",
            accountNumber = "965214",
            accountType = AccountType.CACC
        )
        createPixKeyRequest = CreatePixKeyRequest(
            KeyType.EMAIL,
            key = "yuri@gmail.com",
            bankAccount = bankAccount,
            owner = owner
        )

        createPixKeyResponse = CreatePixKeyResponse(
            keyType = KeyType.EMAIL,
            key = "yuri@gmail.com",
            bankAccount = bankAccount,
            owner = owner,
            createdAt = LocalDateTime.now()
        )

        `when`(itauClient.consultaConta
            (clienteId = requestChavePix.idCliente,
            tipo = requestChavePix.tipoDeConta.name))
            .thenReturn(HttpResponse.ok(contaResponse))

        `when`(bcbClient.cadastraChavePixNoBcb(createPixKeyRequest = createPixKeyRequest))
            .thenReturn(HttpResponse.created(createPixKeyResponse))
    }

    @Test
    fun `deveria cadastrar uma chave pix`() {

        val response = grpcClient.cadastra(this.requestChavePix)

        assertNotNull(response.chavePix)
        assertNotNull(response.pixId)
        assertEquals(response.chavePix, requestChavePix.chavePix)

    }

    @Test
    fun `deveria retornar already exists quando tiver uma chave existente no banco`() {
        val conta = Conta(
            "ITAU BANCO S.A",
            "854712",
            "695847",
            "Yuri Matheus",
            "111-111-111-11"
        )
        val chavePix = ChavePix(
            idCliente = UUID.randomUUID(),
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            tipoDeChave = TipoDeChave.EMAIL,
            chavePix = "yuri@gmail.com",
            conta = conta)
        repository.save(chavePix)

        val error = assertThrows<StatusRuntimeException>() {
            grpcClient.cadastra(requestChavePix)
        }

        assertEquals(Status.ALREADY_EXISTS.code, error.status.code)
        assertEquals("Chave existente", error.status.description)
    }

    @Test
    fun `deveria retornar invalid argument quando os dados nao forem informados`() {

        val request = RegistraChavePixRequest.newBuilder().build()

        val error = assertThrows<StatusRuntimeException>() {
            grpcClient.cadastra(request)
        }

        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
    }

    @Test
    fun `deveria retornar failed precondition quando os dados nao forem encontrado na conta cliente`() {

        `when`(itauClient.consultaConta
            (clienteId = requestChavePix.idCliente,
            tipo = requestChavePix.tipoDeConta.name))
            .thenReturn(HttpResponse.notFound())

        val error = assertThrows<StatusRuntimeException>() {
            grpcClient.cadastra(requestChavePix)
        }

        assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
    }

    @Test
    fun `deveria retorna failed precondition quando o status da resposta do banco brasil for diferente de 201 created`() {
         `when`(bcbClient.cadastraChavePixNoBcb(createPixKeyRequest = createPixKeyRequest))
            .thenReturn(HttpResponse.ok(createPixKeyResponse))

        val error = assertThrows<StatusRuntimeException>() {
            grpcClient.cadastra(requestChavePix)
        }

        assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
        assertEquals("Falha ao registra chave pix no banco do brasil", error.status.description)

    }

    @MockBean(BcbClient::class)
    fun BcbClientMock(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }

    @MockBean(ItauClient::class)
    fun ContaClientMock(): ItauClient {
        return Mockito.mock(ItauClient::class.java)
    }
}

@Factory
class ClientFactory {
    @Bean
    fun stub(@GrpcChannel(GrpcServerChannel.NAME) channel: Channel): PixServiceGrpc.PixServiceBlockingStub {
        return PixServiceGrpc.newBlockingStub(channel)
    }
}

