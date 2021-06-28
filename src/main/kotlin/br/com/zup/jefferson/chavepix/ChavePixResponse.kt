package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.enums.TipoDeChave
import br.com.zup.jefferson.enums.TipoDeConta

data class ChavePixResponse(
    val tipo: TipoDeChave?,
    val chavePix: String,
    val tipoDeConta: TipoDeConta,
    val conta: Conta,) {}