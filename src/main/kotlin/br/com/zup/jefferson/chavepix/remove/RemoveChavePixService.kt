package br.com.zup.jefferson.chavepix.remove

import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.sistemaexterno.BcbClient
import br.com.zup.jefferson.sistemaexterno.DeletePixKeyRequest
import br.com.zup.jefferson.utils.ValidUUID
import br.com.zup.jefferson.utils.exception.ChavePixNotFoundException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChavePixService(
    @Inject val repository: PixRepository,
    @Inject val bcbClient: BcbClient
) {

    @Transactional
    fun removeChavePix(

        @NotBlank @ValidUUID(message = "Formato UUID cliente invalido") idCliente: String?,
        @NotBlank chavePix: String?,
    ) {
        val uuidCliente = UUID.fromString(idCliente)

        val chavePix = repository.findByIdClienteAndChavePix(idCliente = uuidCliente, chavePix = chavePix)
            .orElseThrow { ChavePixNotFoundException("Chave não encontrada ou não pertence ao requisitante") }
        repository.delete(chavePix)

        val bcbRequest = DeletePixKeyRequest(chavePix.chavePix!!)
        val bcbResponse =
            bcbClient.deletaChavePixNoBcb(keys = chavePix.chavePix!!, deletePixKeyRequest = bcbRequest)

        if(bcbResponse.status() != HttpStatus.OK){
            throw IllegalStateException("Falha ao remover chave pix no banco do brasil")
        }
    }

}