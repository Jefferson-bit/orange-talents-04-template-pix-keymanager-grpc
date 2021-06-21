package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.sistemaexterno.ItauClient
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton

@Validated
@Singleton
class RemoveChavePixService(
    @Inject val repository: PixRepository,
    val itauClient: ItauClient,
) {

}