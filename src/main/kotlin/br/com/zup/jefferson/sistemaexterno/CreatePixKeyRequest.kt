package br.com.zup.jefferson.sistemaexterno

import br.com.zup.jefferson.chavepix.ChavePix
import br.com.zup.jefferson.chavepix.ChavePixResponse
import br.com.zup.jefferson.chavepix.Conta
import br.com.zup.jefferson.enums.AccountType
import br.com.zup.jefferson.enums.KeyType
import br.com.zup.jefferson.enums.OwnerType
import br.com.zup.jefferson.enums.TipoDeConta
import java.time.LocalDateTime
import javax.inject.Singleton

data class CreatePixKeyRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
) {}

@Singleton
class ChavePixToCreatePixKeyRequestConvert{

    fun convert(chave: ChavePix): CreatePixKeyRequest {
        val keyType = KeyType.values().associateBy(KeyType::domainType)
        val accountType = AccountType.values().associateBy(AccountType::domainType)
        return CreatePixKeyRequest(
            keyType = keyType[chave.tipoDeChave]!!.convertTipoChaveForKeyType(chave.tipoDeChave)!!,
            key = chave.chavePix!!,
            bankAccount = BankAccount(
                participant = Conta.ITAU_UNIBANCO_SA_ISPB,
                branch = chave.conta.agencia,
                accountNumber = chave.conta.numeroDaConta,
                accountType = accountType[chave.tipoDeConta]!!.tipoContaForAccountType(tipo = chave.tipoDeConta!!)!!
            ),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON, //observação
                name = chave.conta.nomeTitular,
                taxIdNumber = chave.conta.cpfTitular
            )
        )
    }
}


data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType,
) {}

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String,
) {}

data class CreatePixKeyResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime,
) {}

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = Conta.ITAU_UNIBANCO_SA_ISPB,
) {}

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime,
) {}

data class PixKeyDetailsResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime,
){

    fun toModel() : ChavePixResponse{
        return ChavePixResponse(
            tipo = keyType.domainType,
            chavePix = key,
            tipoDeConta = when(this.bankAccount.accountType){
                AccountType.CACC -> TipoDeConta.CONTA_CORRENTE
                AccountType.SVGS -> TipoDeConta.CONTA_POUPANCA
            },
            conta = Conta(
                instituicao = bankAccount.participant,
                agencia = bankAccount.branch,
                numeroDaConta = owner.taxIdNumber,
                nomeTitular = owner.name,
                cpfTitular = owner.taxIdNumber
            )
        )

    }

}