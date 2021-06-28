package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.chavepix.ChavePixResponse
import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.sistemaexterno.BcbClient
import br.com.zup.jefferson.utils.ValidUUID
import br.com.zup.jefferson.utils.exception.ChavePixNotFoundException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class ConsultaChavePixService(
    @Inject val pixRepository: PixRepository,
    @Inject val bcbClient: BcbClient) {

    @Transactional
    fun consultaChavePix(
        @NotBlank @ValidUUID chave: String?): ChavePixResponse {

        val chavePix = pixRepository.findByChavePix(chave)
            .orElseThrow { ChavePixNotFoundException("Chave não encontrada ou não pertence ao requisitante") }

        val bcbResponse = bcbClient.buscaChavePixNoBcb(key = chavePix.chavePix!!)

         bcbResponse.body()?.toModel() ?:
            ChavePixNotFoundException("Essa chave não existe no banco do brasil")

        return  bcbResponse.body().toModel()
    }
}