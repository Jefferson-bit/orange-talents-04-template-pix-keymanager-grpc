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
    val conta: Conta) {



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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChavePixResponse

        if (pixId != other.pixId) return false
        if (tipoDeChave != other.tipoDeChave) return false
        if (chavePix != other.chavePix) return false
        if (tipoDeConta != other.tipoDeConta) return false
        if (conta != other.conta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pixId?.hashCode() ?: 0
        result = 31 * result + (tipoDeChave?.hashCode() ?: 0)
        result = 31 * result + chavePix.hashCode()
        result = 31 * result + tipoDeConta.hashCode()
        result = 31 * result + conta.hashCode()
        return result
    }
}