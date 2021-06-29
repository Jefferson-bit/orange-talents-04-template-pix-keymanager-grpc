package br.com.zup.jefferson.chavepix.remove

import br.com.zup.jefferson.RemoveChavePixRequest
import br.com.zup.jefferson.RemoveChavePixServiceGrpc
import br.com.zup.jefferson.chavepix.ChavePix
import br.com.zup.jefferson.chavepix.Conta
import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.enums.TipoDeChave
import br.com.zup.jefferson.enums.TipoDeConta
import br.com.zup.jefferson.sistemaexterno.BcbClient
import br.com.zup.jefferson.sistemaexterno.DeletePixKeyRequest
import br.com.zup.jefferson.sistemaexterno.DeletePixKeyResponse
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
internal class RemoveChavePixEndpointTestImpl(
    @Inject val repository: PixRepository,
    @Inject val grpcClientRemove: RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub,
) {

    lateinit var request: RemoveChavePixRequest
    lateinit var chaveExistente: ChavePix

    @field:Inject
    lateinit var bcbClient: BcbClient
    lateinit var deletePixKeyRequest: DeletePixKeyRequest
    lateinit var deletePixKeyResponse: DeletePixKeyResponse

    @BeforeEach
    internal fun setUp() {
        val conta = Conta(
            "ITAU BANCO S.A",
            "854712",
            "695847",
            "Yuri Matheus",
            "111-111-111-11"
        )
        chaveExistente = ChavePix(
            idCliente = UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890"),
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            tipoDeChave = TipoDeChave.EMAIL,
            chavePix = "yuri@gmail.com",
            conta = conta)
        repository.save(chaveExistente)

        deletePixKeyRequest = DeletePixKeyRequest(
            chaveExistente.chavePix!!,
            participant = "60701190"
        )
        deletePixKeyResponse = DeletePixKeyResponse(
            deletePixKeyRequest.key,
            participant = "60701190",
            deletedAt = LocalDateTime.now()
        )

        Mockito.`when`(bcbClient.deletaChavePixNoBcb(deletePixKeyRequest.key, deletePixKeyRequest))
            .thenReturn(HttpResponse.ok(deletePixKeyResponse))
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun `deveria excluir chave pix`() {

        request = RemoveChavePixRequest.newBuilder()
            .setIdCliente(chaveExistente.idCliente.toString())
            .setPixId(chaveExistente.pixId)

            .build()

        val response = grpcClientRemove.remove(request)

        assertEquals(chaveExistente.pixId, response.pixId)
        assertEquals(chaveExistente.idCliente.toString(), response.idCliente)
    }

    @Test
    fun `deveria retornar not found quando chave pix nao existir `() {
        val chavePixInexistente = "matheus@gmail.com"
        request = RemoveChavePixRequest.newBuilder()
            .setIdCliente(chaveExistente.idCliente.toString())
            .setPixId(chavePixInexistente)
            .build()

        val error = assertThrows<StatusRuntimeException>() {
            grpcClientRemove.remove(request)
        }

        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave não encontrada ou não pertence ao requisitante", error.status.description)
    }

    @Test
    fun `deveria retornar not found quando id cliente nao existir `() {
        request = RemoveChavePixRequest.newBuilder()
            .setIdCliente(UUID.randomUUID().toString())
            .setPixId(chaveExistente.chavePix)
            .build()

        val error = assertThrows<StatusRuntimeException>() {
            grpcClientRemove.remove(request)
        }

        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave não encontrada ou não pertence ao requisitante", error.status.description)
    }

    @Test
    fun `deveria retornar invalid argument quando chave pix e id cliente nao for informado ou formato UUID for invalido`() {
        request = RemoveChavePixRequest.newBuilder().build()

        val error = assertThrows<StatusRuntimeException>() {
            grpcClientRemove.remove(request)
        }

        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
        assertEquals("Parametro Invalido", error.status.description)

    }

    @Test
    fun `deveria retornar failed precondition quando o banco do brasil retornar um status diferente de 200 ok`() {

        Mockito.`when`(bcbClient.deletaChavePixNoBcb(deletePixKeyRequest.key, deletePixKeyRequest))
            .thenReturn(HttpResponse.created(deletePixKeyResponse))

        request = RemoveChavePixRequest.newBuilder()
            .setIdCliente(chaveExistente.idCliente.toString())
            .setPixId(chaveExistente.pixId)
            .build()
        val error = assertThrows<StatusRuntimeException>() {
            grpcClientRemove.remove(request)
        }
        assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
        assertEquals("Falha ao remover chave pix no banco do brasil", error.status.description)
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
    fun stub(@GrpcChannel(GrpcServerChannel.NAME) channel: Channel): RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub {
        return RemoveChavePixServiceGrpc.newBlockingStub(channel)
    }
}