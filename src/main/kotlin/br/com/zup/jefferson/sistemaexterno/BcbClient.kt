package br.com.zup.jefferson.sistemaexterno

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client(value = "\${bcb.url}")
interface BcbClient {

    @Post( value = "/api/v1/pix/keys",
        consumes =  [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML] )
    fun cadastraChavePixNoBcb(@Body createPixKeyRequest: CreatePixKeyRequest) : HttpResponse<CreatePixKeyResponse>

    @Delete( value = "/api/v1/pix/keys/{keys}/",
        consumes =  [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML] )
    fun deletaChavePixNoBcb( @PathVariable keys: String ,@Body deletePixKeyRequest: DeletePixKeyRequest) : HttpResponse<DeletePixKeyResponse>

    @Get(value = "/api/v1/pix/keys/{key}",consumes =  [MediaType.APPLICATION_XML])
    fun buscaChavePixNoBcb(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>
}