package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.chavepix.ChavePixResponse
import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.sistemaexterno.BcbClient
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
open class ConsultaChavePorChavePix(
    @field:NotBlank @Size(max = 77) val chave: String
    ) : ConsultaChave {
    override fun consulta(pixRepository: PixRepository, bcbClient: BcbClient): ChavePixResponse {
        val response = bcbClient.buscaChavePixNoBcb(key = chave!!)
        return response.body().toModel()
    }
}