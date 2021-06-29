package br.com.zup.jefferson.chavepix.lista

import br.com.zup.jefferson.chavepix.ChavePix

class DetalhesChavePixResponse(chavePix: ChavePix) {

    val pixId = chavePix.pixId
    val idCliente = chavePix.idCliente
    val tipoDeChave = chavePix.tipoDeChave
    val chavePix = chavePix.chavePix
    val tipoDeConta = chavePix.tipoDeConta
    val createdAt = chavePix.criadoEm
}