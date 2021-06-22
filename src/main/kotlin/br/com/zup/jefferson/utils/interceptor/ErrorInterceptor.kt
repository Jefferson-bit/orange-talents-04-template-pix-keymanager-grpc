package br.com.zup.jefferson.utils.interceptor

import br.com.zup.jefferson.utils.exception.ChavePixAlreadyExistsException
import br.com.zup.jefferson.utils.exception.ChavePixNotFoundException
import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(InterceptorErrorAdvice::class)
class ErrorInterceptor : MethodInterceptor<Any, Any>{
    override fun intercept(context: MethodInvocationContext<Any, Any?>): Any? {

       return try {
             context.proceed()
        }catch (e: Exception){
            val error = when(e){
                is ChavePixNotFoundException -> Status.NOT_FOUND.withCause(e)
                    .withDescription(e.message).asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withCause(e)
                    .withDescription(e.message).asRuntimeException()
                is ChavePixAlreadyExistsException -> Status.ALREADY_EXISTS.withDescription(e.message)
                    .asRuntimeException()
                is ConstraintViolationException -> handlerConstraintViolationException(e)
                else -> Status.UNKNOWN.withDescription("Um erro inesperado ocorreu").asRuntimeException()
            }
            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            responseObserver.onError(error)
        }

    }

    /**
     * método extrai a campo e descrição da exceção que venha de ConstraintViolationException
     */
    private fun handlerConstraintViolationException(e: ConstraintViolationException): StatusRuntimeException {
        val details = BadRequest.newBuilder()
            .addAllFieldViolations(e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.propertyPath.last().name)
                    .setDescription(it.message)
                    .build()
            })
            .build()
        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("Parametro Invalido")
            .addDetails(com.google.protobuf.Any.pack(details))
            .build()
        return StatusProto.toStatusRuntimeException(statusProto)
    }
}