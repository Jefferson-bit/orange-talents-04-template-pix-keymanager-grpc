package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.*
import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.sistemaexterno.BcbClient
import br.com.zup.jefferson.utils.interceptor.InterceptorErrorAdvice
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@InterceptorErrorAdvice
@Singleton
class ConsultaChavePixEndpoint(
    @Inject val pixRepository: PixRepository,
    @Inject val bcbClient: BcbClient
) : ConsultaDadosChavePixServiceGrpc.ConsultaDadosChavePixServiceImplBase(){

    override fun consulta(request: ConsultaPixRequest?, responseObserver: StreamObserver<ConsultaPixResponse>?) {

        val request = request!!.toModel()
        val serviceResponse = request.consulta(pixRepository = pixRepository, bcbClient = bcbClient)

        val instant = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()

        val createAt = Timestamp.newBuilder()
            .setNanos(instant.nano)
            .setSeconds(instant.epochSecond)
            .build()

        val response = ConsultaPixResponse.newBuilder()
            .setClienteId(serviceResponse.clienteId?.toString() ?: "")
            .setPixId(serviceResponse.pixId ?: "")
            .setChave(ConsultaPixResponse.ChavePix.newBuilder()
                .setChavePix(serviceResponse.chavePix)
                .setTipoDeChave(TipoDeChave.valueOf(serviceResponse.tipoDeChave!!.name))
                .setCreateAt(createAt)
                .setConta(ConsultaPixResponse.ChavePix.Conta.newBuilder()
                    .setInstituicao(serviceResponse.conta.instituicao)
                    .setAgencia(serviceResponse.conta.agencia)
                    .setNome(serviceResponse.conta.nomeTitular)
                    .setCpf(serviceResponse.conta.cpfTitular)
                    .setNumero(serviceResponse.conta.numeroDaConta)
                    .setTipoDeConta(TipoDeConta.valueOf(serviceResponse.tipoDeConta.name))
                    .build())
                .build())
            .build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}