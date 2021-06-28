package br.com.zup.jefferson.chavepix

import br.com.zup.jefferson.enums.TipoDeChave
import br.com.zup.jefferson.enums.TipoDeConta
import br.com.zup.jefferson.sistemaexterno.CreatePixKeyRequest
import br.com.zup.jefferson.utils.ValidUUID
import br.com.zup.jefferson.utils.ValidaChavePix
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidaChavePix
data class NovaChavePixRequest(
    @field:NotBlank @field:ValidUUID val idCliente: String?,
    @field:Size(max = 77) val chavePix: String?,
    @field:NotNull val tipoDeConta: TipoDeConta?,
    @field:NotNull val tipoDeChave: TipoDeChave?

    ) {

    fun toModel(conta: Conta) : ChavePix {
        return ChavePix(idCliente = UUID.fromString(this.idCliente),
                        tipoDeConta = TipoDeConta.valueOf(this.tipoDeConta!!.name),
                        tipoDeChave = TipoDeChave.valueOf(this.tipoDeChave!!.name),
                        chavePix,
                        conta)
    }
}