package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.chavepix.ChavePixResponse
import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.sistemaexterno.BcbClient
import br.com.zup.jefferson.utils.ValidUUID
import br.com.zup.jefferson.utils.exception.ChavePixNotFoundException
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.constraints.NotBlank

@Introspected
data class ConsultaChavePorPixId(
    @field:NotBlank  val pixId: String?,
    @field:NotBlank @field:ValidUUID val clienteId: String?,
): ConsultaChave {
    override fun consulta(pixRepository: PixRepository, bcbClient: BcbClient): ChavePixResponse {
        val uuidCliente = UUID.fromString(clienteId)
        val chavePix = pixRepository.findByIdClienteAndPixId(idCliente = uuidCliente, pixId = pixId)
            .orElseThrow { ChavePixNotFoundException("Chave não encontrada ou não pertence ao requisitante") }
        return ChavePixResponse.of(chavePix)
    }
}