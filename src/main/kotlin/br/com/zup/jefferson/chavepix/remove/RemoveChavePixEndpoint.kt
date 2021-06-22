package br.com.zup.jefferson.chavepix.remove

import br.com.zup.jefferson.RemoveChavePixRequest
import br.com.zup.jefferson.RemoveChavePixResponse
import br.com.zup.jefferson.RemoveChavePixServiceGrpc
import br.com.zup.jefferson.utils.interceptor.InterceptorErrorAdvice
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@InterceptorErrorAdvice
@Singleton
class RemoveChavePixEndpoint(@Inject val service: RemoveChavePixService)
    : RemoveChavePixServiceGrpc.RemoveChavePixServiceImplBase() {


    override fun remove(request: RemoveChavePixRequest?, responseObserver: StreamObserver<RemoveChavePixResponse>?) {
        service.removeChavePix(request!!.idCliente, request?.chavePix)

        val response = RemoveChavePixResponse.newBuilder()
            .setChavePix(request.chavePix)
            .setIdCliente(request.idCliente)
            .setMessage(Status.OK
                .withDescription("Chave excluida com sucesso").description)
            .build()
        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}