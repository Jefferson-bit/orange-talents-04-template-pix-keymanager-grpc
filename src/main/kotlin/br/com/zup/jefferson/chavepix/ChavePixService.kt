package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.enums.AccountType
import br.com.zup.jefferson.enums.KeyType
import br.com.zup.jefferson.enums.TipoDeChave
import br.com.zup.jefferson.sistemaexterno.BcbClient
import br.com.zup.jefferson.sistemaexterno.ChavePixToCreatePixKeyRequestConvert
import br.com.zup.jefferson.sistemaexterno.CreatePixKeyRequest
import br.com.zup.jefferson.sistemaexterno.ItauClient
import br.com.zup.jefferson.utils.exception.ChavePixAlreadyExistsException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService(
    @Inject val repository: PixRepository,
    @Inject val itauClient: ItauClient,
    @Inject val bcbClient: BcbClient,
    @Inject val chavePixConvert: ChavePixToCreatePixKeyRequestConvert

) {

    @Transactional
    fun salvaChavePix(@Valid novaChave: NovaChavePixRequest): ChavePix {

        if (repository.existsByChavePix(novaChave.chavePix!!)) {
            throw ChavePixAlreadyExistsException("Chave existente")
        }

        val response =
            itauClient.consultaConta(clienteId = novaChave.idCliente!!, tipo = novaChave.tipoDeConta!!.name)
        val conta = response.body()?.toModel() ?: throw IllegalStateException("Conta não encontrada")

        val chavePix = novaChave.toModel(conta)
        repository.save(chavePix)

        val bcbRequest = chavePixConvert.convert(chavePix)
        val bcbResponse = bcbClient.cadastraChavePixNoBcb(createPixKeyRequest = bcbRequest)

        if(bcbResponse.status() != HttpStatus.CREATED){
            throw IllegalStateException("Falha ao registra chave pix no banco do brasil")
        }

        chavePix.atualizaChavePix(bcbResponse.body()?.key)
        return chavePix

    }
}
