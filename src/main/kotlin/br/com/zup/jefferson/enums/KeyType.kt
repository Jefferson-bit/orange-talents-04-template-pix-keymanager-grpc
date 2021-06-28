package br.com.zup.jefferson.enums

import java.lang.IllegalStateException

enum class KeyType(val domainType: TipoDeChave?) {

    CPF(TipoDeChave.CPF) {
        override fun convertTipoChaveForKeyType(tipoDeChave: TipoDeChave?): KeyType {
            return when(tipoDeChave){
                TipoDeChave.CPF -> CPF
                else -> throw IllegalStateException("Argumento invalido")
            }
        }
    },

    CNPJ(null) {
        override fun convertTipoChaveForKeyType(tipoDeChave: TipoDeChave?): KeyType? {
            return if(tipoDeChave ==null ) CNPJ else null
        }
    },

    EMAIL(TipoDeChave.EMAIL) {
        override fun convertTipoChaveForKeyType(tipoDeChave: TipoDeChave?): KeyType {
            return when(tipoDeChave){
                TipoDeChave.EMAIL -> EMAIL
                else -> throw IllegalStateException("Argumento invalido")
            }
        }
    },

    PHONE(TipoDeChave.NUMERO_CELULAR) {
        override fun convertTipoChaveForKeyType(tipoDeChave: TipoDeChave?): KeyType {
            return when(tipoDeChave){
                TipoDeChave.NUMERO_CELULAR -> PHONE
                else -> throw IllegalStateException("Argumento invalido")
            }
        }
    },

    RANDOM(TipoDeChave.CHAVE_ALEATORIA) {
        override fun convertTipoChaveForKeyType(tipoDeChave: TipoDeChave?): KeyType {
            return when(tipoDeChave){
                TipoDeChave.CHAVE_ALEATORIA -> RANDOM
                else -> throw IllegalStateException("Argumento invalido")
            }
        }
    };

    abstract fun convertTipoChaveForKeyType(tipoDeChave: TipoDeChave?) : KeyType?

}
