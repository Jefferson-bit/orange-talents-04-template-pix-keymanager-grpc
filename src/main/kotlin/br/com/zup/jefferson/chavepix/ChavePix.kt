package br.com.zup.jefferson.chavepix
import br.com.zup.jefferson.enums.TipoDeChave
import br.com.zup.jefferson.enums.TipoDeConta
import br.com.zup.jefferson.sistemaexterno.CreatePixKeyRequest
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(
    @Column(nullable = false)
    val idCliente: UUID,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeConta: TipoDeConta?,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeChave: TipoDeChave?,
    var chavePix: String?,
    @Embedded
    val conta: Conta,
) {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator",)
    var pixId: String? = null

    fun atualizaChavePix(chavePix: String?){
        this.chavePix = chavePix
    }

    val criadoEm: LocalDateTime = LocalDateTime.now()

    @PrePersist
    fun createdAt(){
        criadoEm
    }
}

@Embeddable
class Conta(
    @field:NotBlank val instituicao: String,
    @field:NotBlank val agencia: String,
    @field:NotBlank val numeroDaConta: String,
    @field:NotBlank val nomeTitular: String,
    @field:NotBlank val cpfTitular: String

) {
    companion object{
        val  ITAU_UNIBANCO_SA_ISPB = "60701190"
    }
}
