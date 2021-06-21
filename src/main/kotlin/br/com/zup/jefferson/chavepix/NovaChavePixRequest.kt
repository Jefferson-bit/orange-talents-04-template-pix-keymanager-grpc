package br.com.zup.jefferson.chavepix

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
    @field:NotBlank @Size(max = 77) val chavePix: String?,
    @field:NotNull val tipoDeConta: TipoDeConta?,
    @field:NotNull val tipoDeChave: TipoDeChave?

    ) {

    fun toModel(conta: Conta) : ChavePix {
        val tipo = if(this.tipoDeChave == TipoDeChave.CHAVE_ALEATORIA) UUID.randomUUID().toString() else this.chavePix

        return ChavePix(idCliente = UUID.fromString(this.idCliente),
                        tipoDeConta = TipoDeConta.valueOf(this.tipoDeConta!!.name),
                        tipoDeChave = TipoDeChave.valueOf(this.tipoDeChave!!.name),
                        tipo,
                        conta)
    }

}