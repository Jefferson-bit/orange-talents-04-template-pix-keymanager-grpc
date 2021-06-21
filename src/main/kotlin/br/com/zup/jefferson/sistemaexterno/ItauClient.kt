package br.com.zup.jefferson.sistemaexterno

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${itau.url}" )
interface ItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas", consumes = [MediaType.APPLICATION_JSON])
    fun consultaConta(@PathVariable clienteId: String, @QueryValue tipo: String ) : HttpResponse<ContaResponse>

    @Get("/api/v1/clientes/{clienteId}", consumes = [MediaType.APPLICATION_JSON])
    fun consultaCliente(@PathVariable clienteId: String) : HttpResponse<ClienteResponse>
}