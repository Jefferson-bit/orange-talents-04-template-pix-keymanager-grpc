package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.*
import br.com.zup.jefferson.utils.interceptor.InterceptorErrorAdvice
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@InterceptorErrorAdvice
@Singleton
class ChavePixEndpoint(@Inject val service: ChavePixService)
    : PixServiceGrpc.PixServiceImplBase() {

    override fun cadastra(
        request: RegistraChavePixRequest?,
        responseObserver: StreamObserver<RegistraChavePixResponse>?, ) {

        val chavePix = request!!.toModel()
        val pixService = service.salvaChavePix(chavePix)

        val response = RegistraChavePixResponse.newBuilder()
            .setPixId(pixService.pixId)
            .setChavePix(pixService.chavePix)
            .build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
//        val sincronizedManager: SynchronousTransactionManager<Any>,
    }
}
