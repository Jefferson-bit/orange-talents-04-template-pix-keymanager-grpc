package br.com.zup.jefferson.health

import br.com.zup.jefferson.HealthCheckRequest
import br.com.zup.jefferson.HealthCheckResponse
import br.com.zup.jefferson.HealthGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class HealhCheckService : HealthGrpc.HealthImplBase() {

    override fun check(request: HealthCheckRequest?, responseObserver: StreamObserver<HealthCheckResponse>?) {

        responseObserver!!.onNext(HealthCheckResponse.newBuilder().apply {
            status = HealthCheckResponse.ServingStatus.SERVING
        }.build())

        responseObserver.onCompleted()
    }

    override fun watch(request: HealthCheckRequest?, responseObserver: StreamObserver<HealthCheckResponse>?) {
        responseObserver!!.onNext(HealthCheckResponse.newBuilder().apply {
            status = HealthCheckResponse.ServingStatus.SERVING
        }.build())
    }
}