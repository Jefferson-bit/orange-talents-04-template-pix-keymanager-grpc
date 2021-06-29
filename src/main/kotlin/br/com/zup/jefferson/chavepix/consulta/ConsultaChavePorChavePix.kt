package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.chavepix.ChavePixResponse
import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.sistemaexterno.BcbClient
import br.com.zup.jefferson.utils.exception.ChavePixNotFoundException
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class ConsultaChavePorChavePix(
    @field:NotBlank @Size(max = 77) val chave: String,
) : ConsultaChave {
    override fun consulta(pixRepository: PixRepository, bcbClient: BcbClient): ChavePixResponse {
        val response = bcbClient.buscaChavePixNoBcb(key = chave!!)
        if (response.status == HttpStatus.OK) {
            return response.body().toModel()
        }
        return throw ChavePixNotFoundException("Chave pix não foi encontrada")
    }

}