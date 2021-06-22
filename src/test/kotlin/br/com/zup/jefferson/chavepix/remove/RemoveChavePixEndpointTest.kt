package br.com.zup.jefferson.chavepix.remove

import br.com.zup.jefferson.RemoveChavePixRequest
import br.com.zup.jefferson.RemoveChavePixServiceGrpc
import br.com.zup.jefferson.chavepix.*
import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChavePixEndpointTest(
    @Inject val repository: PixRepository,
    @Inject val grpcClientRemove: RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub
) {

    lateinit var request: RemoveChavePixRequest
    lateinit var chaveExistente: ChavePix

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
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun `deveria excluir chave pix`(){

        request = RemoveChavePixRequest.newBuilder()
            .setIdCliente(chaveExistente.idCliente.toString())
            .setChavePix(chaveExistente.chavePix)
            .build()

        val response = grpcClientRemove.remove(request)

        assertEquals(chaveExistente.chavePix, response.chavePix)
        assertEquals(chaveExistente.idCliente.toString(), response.idCliente)
    }

    @Test
    fun `deveria retornar not found quando chave pix nao existir `(){
        val chavePixInexistente = "matheus@gmail.com"
        request = RemoveChavePixRequest.newBuilder()
            .setIdCliente(chaveExistente.idCliente.toString())
            .setChavePix(chavePixInexistente)
            .build()

        val error = assertThrows<StatusRuntimeException>() {
            grpcClientRemove.remove(request)
        }

        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave não encontrada ou não pertence ao requisitante", error.status.description)
    }

    @Test
    fun `deveria retornar not found quando id cliente nao existir `(){
        request = RemoveChavePixRequest.newBuilder()
            .setIdCliente(UUID.randomUUID().toString())
            .setChavePix(chaveExistente.chavePix)
            .build()

        val error = assertThrows<StatusRuntimeException>() {
            grpcClientRemove.remove(request)
        }

        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave não encontrada ou não pertence ao requisitante", error.status.description)
    }

    @Test
    fun `deveria retornar invalid argument quando chave pix e id cliente nao for informado ou formato UUID for invalido`(){
        request = RemoveChavePixRequest.newBuilder().build()

        val error = assertThrows<StatusRuntimeException>() {
            grpcClientRemove.remove(request)
        }

        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)

    }

}

@Factory
class ClientFactory {
    @Bean
    fun stub(@GrpcChannel(GrpcServerChannel.NAME) channel: Channel): RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub {
        return RemoveChavePixServiceGrpc.newBlockingStub(channel)
    }
}