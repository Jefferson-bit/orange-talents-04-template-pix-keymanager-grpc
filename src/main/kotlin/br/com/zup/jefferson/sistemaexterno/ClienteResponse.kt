package br.com.zup.jefferson.sistemaexterno

data class ClienteResponse(
    val id: String,
    val nome: String,
    val cpf: String,
    val instituicao: InstituicaoResponse
) {
}

