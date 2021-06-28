package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.ConsultaPixRequest
import br.com.zup.jefferson.ConsultaPixRequest.FiltroCase.PIXID

fun ConsultaPixRequest.toModel(): ConsultaChave {

    return when (filtroCase) {
        PIXID -> pixId.let {
            ConsultaChavePorPixId(pixId = it.pixId, clienteId = it.clienteId)
        }
        else -> ConsultaChavePorChavePix(chave = chavePix)
    }
}