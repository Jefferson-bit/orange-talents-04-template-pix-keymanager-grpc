package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.RegistraChavePixRequest
import br.com.zup.jefferson.TipoDeChave.DESCONHECIDO_CHAVE
import br.com.zup.jefferson.TipoDeConta.DESCONHECIDO_CONTA
import br.com.zup.jefferson.enums.TipoDeChave
import br.com.zup.jefferson.enums.TipoDeConta

fun RegistraChavePixRequest.toModel(): NovaChavePixRequest {
    return NovaChavePixRequest(
        idCliente = idCliente,
        chavePix = chavePix,

        tipoDeConta = when (tipoDeConta) {
            DESCONHECIDO_CONTA -> null
            else -> TipoDeConta.valueOf(tipoDeConta.name)
        },

        tipoDeChave = when (tipoDeChave) {
            DESCONHECIDO_CHAVE -> null
            else -> TipoDeChave.valueOf(tipoDeChave.name)
        }
    )
}
