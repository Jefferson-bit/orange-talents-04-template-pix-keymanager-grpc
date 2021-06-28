package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.*
import br.com.zup.jefferson.utils.interceptor.InterceptorErrorAdvice
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@InterceptorErrorAdvice
@Singleton
class ConsultaChavePixEndpoint(
    @Inject val service: ConsultaChavePixService
) : ConsultaDadosChavePixServiceGrpc.ConsultaDadosChavePixServiceImplBase(){

    override fun consulta(request: ConsultaPixRequest?, responseObserver: StreamObserver<ConsultaPixResonse>?) {
        val chavePix = request!!.chavePix
        val consultaChavePix = service.consultaChavePix(chavePix)

        val instant = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()

        val createAt = Timestamp.newBuilder()
            .setNanos(instant.nano)
            .setSeconds(instant.epochSecond)
            .build()

        val response = ConsultaPixResonse.newBuilder()
            .setChavePix(consultaChavePix!!.chavePix)
            .setNome(consultaChavePix.conta.nomeTitular)
            .setCpf(consultaChavePix.conta.cpfTitular)
            .setTipoDeChave(TipoDeChave.valueOf(consultaChavePix.tipo!!.name))
            .addConta(ConsultaPixResonse.Conta.newBuilder()
                .setInstituicao(consultaChavePix.conta.instituicao)
                .setAgencia(consultaChavePix.conta.agencia)
                .setNumero(consultaChavePix.conta.numeroDaConta)
                .setTipoDeConta(TipoDeConta.valueOf(consultaChavePix.tipoDeConta.name))
                .build())
            .setCreateAt(createAt)
            .build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}