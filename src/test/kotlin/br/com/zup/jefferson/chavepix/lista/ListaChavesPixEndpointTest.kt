package br.com.zup.jefferson.chavepix.lista

import br.com.zup.jefferson.ListaChavesPixGrpc
import br.com.zup.jefferson.ListaChavesPixRequest
import br.com.zup.jefferson.chavepix.ChavePix
import br.com.zup.jefferson.chavepix.Conta
import br.com.zup.jefferson.chavepix.Instituicoes
import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.enums.TipoDeChave
import br.com.zup.jefferson.enums.TipoDeConta
import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)
internal class ListaChavesPixEndpointTest(
    @Inject val repository: PixRepository,
    @Inject val grpcLista: ListaChavesPixGrpc.ListaChavesPixBlockingStub,
) {

    val uuidCliente = UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890")

    @BeforeEach
    internal fun setUp() {
        repository.save(chave(tipoDeChave = TipoDeChave.CPF, chave = "07278778107", idCliente = uuidCliente))
        repository.save(chave(tipoDeChave = TipoDeChave.NUMERO_CELULAR,
            chave = "+71999656180",
            idCliente = uuidCliente))
        repository.save(chave(tipoDeChave = TipoDeChave.EMAIL, chave = "romaria@gmail.com", idCliente = uuidCliente))
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun `deveria listar todas as chaves pix`() {
        val request = ListaChavesPixRequest.newBuilder()
            .setIdCliente(uuidCliente.toString())
            .build()
        val response = grpcLista.lista(request)

        assertEquals(3, response.chavesPixList.size)
    }

    @Test
    fun `deveria retornar lista vazia quando cliente nao possuir chaves`(){
        val request = ListaChavesPixRequest.newBuilder()
            .setIdCliente(UUID.randomUUID().toString())
            .build()

        val response = grpcLista.lista(request)

        assertEquals(0, response.chavesPixCount)
    }

    @Test
    fun `deveria retorna invalid argument quando o id do cliente nao for informado`() {
        val request = ListaChavesPixRequest.newBuilder().build()
        val error = assertThrows<StatusRuntimeException> {
            grpcLista.lista(request)
        }
        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
        assertEquals("Id do cliente não pode ser nulo ou vazio", error.status.description)

    }

    @Factory
    class ClientFactory {
        @Bean
        fun stub(@GrpcChannel(GrpcServerChannel.NAME) channel: Channel): ListaChavesPixGrpc.ListaChavesPixBlockingStub {
            return ListaChavesPixGrpc.newBlockingStub(channel)
        }
    }

    private fun chave(
        tipoDeChave: TipoDeChave,
        chave: String,
        idCliente: UUID,

        ): ChavePix {

        return ChavePix(
            idCliente = idCliente,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            tipoDeChave = tipoDeChave,
            chavePix = chave,
            conta = Conta(
                instituicao = Instituicoes.nome("60701190"),
                agencia = "0001",
                numeroDaConta = "291900",
                nomeTitular = "Rafael M C Ponte",
                cpfTitular = "02467781054"
            )

        )
    }
}
