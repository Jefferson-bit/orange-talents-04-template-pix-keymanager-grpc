package br.com.zup.jefferson.chavepix.remove

import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.sistemaexterno.ItauClient
import br.com.zup.jefferson.utils.ValidUUID
import br.com.zup.jefferson.utils.exception.ChavePixNotFoundException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChavePixService(@Inject val repository: PixRepository) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun removeChavePix(
        @NotBlank @ValidUUID(message = "Formato UUID cliente invalido") idCliente: String?,
        @NotBlank chavePix: String?,
    ) {

        val uuidCliente = UUID.fromString(idCliente)

        val buscaChavePix = repository.findByIdClienteAndChavePix(idCliente = uuidCliente, chavePix = chavePix)
            .orElseThrow { ChavePixNotFoundException("Chave não encontrada ou não pertence ao requisitante") }

        repository.delete(buscaChavePix)

    }

}