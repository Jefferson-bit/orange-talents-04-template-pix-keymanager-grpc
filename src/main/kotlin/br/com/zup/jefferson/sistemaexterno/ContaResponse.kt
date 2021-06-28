package br.com.zup.jefferson.sistemaexterno

import br.com.zup.jefferson.chavepix.Conta

data class ContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse,

    ) {

    fun toModel() : Conta {
        return Conta(
            instituicao = this.instituicao.nome,
            agencia = agencia,
            numeroDaConta = this.numero,
            nomeTitular = this.titular.nome,
            cpfTitular = this.titular.cpf)
    }

}

data class TitularResponse(val nome: String, val cpf: String) {}

data class InstituicaoResponse(val nome: String, val ispb: String) {}
