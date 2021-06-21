package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.sistemaexterno.ItauClient
import br.com.zup.jefferson.utils.exception.ChavePixAlreadyExistsException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService(@Inject val repository: PixRepository, @Inject val itauClient: ItauClient) {

    @Transactional
    fun salvaChavePix(@Valid novaChave: NovaChavePixRequest): ChavePix {

        if (repository.existsByChavePix(novaChave.chavePix!!)) {
            throw ChavePixAlreadyExistsException("Chave ${novaChave.chavePix} existente")
        }

        val response =
            itauClient.consultaConta(clienteId = novaChave.idCliente!!, tipo = novaChave.tipoDeConta!!.name)
        val conta = response.body()?.toModel() ?: throw IllegalStateException("Conta não encontrada")

        val chavePix = novaChave.toModel(conta)
        repository.save(chavePix)
        return chavePix

    }

}
