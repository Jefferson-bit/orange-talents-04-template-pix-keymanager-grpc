package br.com.zup.jefferson.enums


enum class AccountType( val domainType: TipoDeConta) {

    CACC(TipoDeConta.CONTA_CORRENTE) {
        override fun tipoContaForAccountType(tipo: TipoDeConta): AccountType {
            return when (tipo) {
                TipoDeConta.CONTA_CORRENTE -> CACC
                else -> throw IllegalStateException("Argumento invalido")
            }
        }
    },
    SVGS(TipoDeConta.CONTA_POUPANCA) {
        override fun tipoContaForAccountType(tipo: TipoDeConta): AccountType {
            return when (tipo) {
                TipoDeConta.CONTA_POUPANCA -> SVGS
                else -> throw IllegalStateException("Argumento invalido")
            }
        }
    };

    abstract fun tipoContaForAccountType(tipo: TipoDeConta): AccountType
}