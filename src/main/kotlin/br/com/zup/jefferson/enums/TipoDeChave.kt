package br.com.zup.jefferson.enums


import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoDeChave {

    CPF {
        override fun validaChave(chavePix: String?): Boolean {
            if(chavePix.isNullOrBlank()){
                return false
            }
            if(!chavePix.matches("^[0-9]{11}\$".toRegex())){
                return false
            }

            return CPFValidator().run {
                initialize(null)
                isValid(chavePix, null)
            }
        }
    },

    EMAIL {
        override fun validaChave(chavePix: String?): Boolean {
            if(chavePix.isNullOrBlank()){
                return false
            }
            return EmailValidator().run {
                initialize(null)
                isValid(chavePix, null)
            }
        }
    },

    NUMERO_CELULAR {
        override fun validaChave(chavePix: String?): Boolean {
            if(chavePix.isNullOrBlank()){
                return false
            }
            return chavePix.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },

    CHAVE_ALEATORIA {
        override fun validaChave(chavePix: String?) = chavePix.isNullOrBlank()
    };

    abstract fun validaChave(chavePix: String?) : Boolean
}