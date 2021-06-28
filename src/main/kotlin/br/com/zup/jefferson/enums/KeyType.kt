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
    companion object {
        fun bla(tipoDeChave: TipoDeChave?) :KeyType {
            val associateBy = KeyType.values().associateBy(KeyType::domainType)
            return associateBy[tipoDeChave]!!
        }
    }

//    CPF(TipoDeChave.CPF),
//    CNPJ(null),
//    EMAIL(TipoDeChave.EMAIL),
//    PHONE(TipoDeChave.NUMERO_CELULAR),
//    RANDOM(TipoDeChave.CHAVE_ALEATORIA);
//
//    companion object {
//        private val mapping = values().associateBy(KeyType::domainType)
//        fun by(domainType: TipoDeChave?): KeyType {
//            return mapping[domainType] ?: throw IllegalArgumentException("Argumento invalido")
//
//        }
//    }

//    companion object {
//        fun converteTipoDominioParaKeyType() = mapOf(
//            Pair(TipoDeChave.CHAVE_ALEATORIA, KeyType.RANDOM),
//            Pair(TipoDeChave.CPF, KeyType.CPF),
//            Pair(TipoDeChave.NUMERO_CELULAR, KeyType.PHONE),
//            Pair(TipoDeChave.EMAIL, KeyType.EMAIL),
//            null to CNPJ
//        )
//
//    }
}
