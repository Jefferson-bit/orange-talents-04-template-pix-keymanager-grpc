package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.enums.TipoDeChave
import br.com.zup.jefferson.enums.TipoDeConta
import java.util.*

data class ChavePixResponse(
    val pixId: String? = null,
    val clienteId: UUID? = null,
    val tipoDeChave: TipoDeChave?,
    val chavePix: String,
    val tipoDeConta: TipoDeConta,
    val conta: Conta,) {

    companion object {
        fun of(chavePix: ChavePix): ChavePixResponse {
            return ChavePixResponse(
                pixId = chavePix.pixId,
                clienteId = chavePix.idCliente,
                tipoDeChave = chavePix.tipoDeChave,
                chavePix = chavePix.chavePix!!,
                tipoDeConta = chavePix.tipoDeConta!!,
                conta = chavePix.conta

            )
        }
    }
}