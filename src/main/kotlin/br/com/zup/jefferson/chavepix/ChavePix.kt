package br.com.zup.jefferson.chavepix
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

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
    @Column(nullable = false)
    val chavePix: String?,
    @Embedded
    val conta: Conta
) {



    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator",)
    var pixId: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChavePix

        if (idCliente != other.idCliente) return false

        return true
    }

    override fun hashCode(): Int {
        return idCliente.hashCode()
    }
}

@Embeddable
class Conta(
    val instituicao: String,
    val agencia: String,
    val numeroDaConta: String,
    val nomeTitular: String,
    val cpfTitular: String
) {}
