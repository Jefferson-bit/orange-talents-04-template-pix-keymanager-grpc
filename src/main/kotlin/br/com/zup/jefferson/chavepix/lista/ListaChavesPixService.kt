package br.com.zup.jefferson.chavepix.lista

import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.utils.ValidUUID
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class ListaChavesPixService(@Inject val pixRepository: PixRepository) {

    fun lista(@NotBlank @ValidUUID idCliente: String?): List<DetalhesChavePixResponse> {
        val uuidCliente = UUID.fromString(idCliente)
        return pixRepository.listAllChavePix(idCliente = uuidCliente).map { obj ->
            DetalhesChavePixResponse(obj)
        }
    }
}