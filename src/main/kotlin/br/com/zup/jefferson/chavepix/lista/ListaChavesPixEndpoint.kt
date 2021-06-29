package br.com.zup.jefferson.chavepix.lista

import br.com.zup.jefferson.*
import br.com.zup.jefferson.utils.interceptor.InterceptorErrorAdvice
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.lang.IllegalArgumentException
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@InterceptorErrorAdvice
@Singleton
class ListaChavesPixEndpoint(@Inject val service: ListaChavesPixService) : ListaChavesPixGrpc.ListaChavesPixImplBase() {

    override fun lista(request: ListaChavesPixRequest?, responseObserver: StreamObserver<ListaChavesPixResponse>?) {

        if(request!!.idCliente.isNullOrBlank()){
            throw IllegalArgumentException("Id do cliente não pode ser nulo ou vazio")
        }

        val serviceRequest = service.lista(idCliente = request?.idCliente)
         val lista = serviceRequest.map { obj ->
                ListaChavesPixResponse.ChavesPix.newBuilder()
                    .setPixId(obj.pixId)
                    .setTipoDeChave(TipoDeChave.valueOf(obj.tipoDeChave!!.name))
                    .setTipoDeConta(TipoDeConta.valueOf(obj.tipoDeConta!!.name))
                    .setChavePix(obj.chavePix)
                    .setCreateAt(obj.createdAt.let {
                        val createAt = it.atZone(ZoneId.of("UTC")).toInstant()
                            Timestamp.newBuilder()
                                .setNanos(createAt.nano)
                                .setSeconds(createAt.epochSecond)
                                .build()
                    })
                    .build()
            }
            responseObserver!!.onNext(ListaChavesPixResponse.newBuilder()
                .setIdCliente(request!!.idCliente)
                .addAllChavesPix(lista)
                .build())
            responseObserver.onCompleted()
        }
    }
