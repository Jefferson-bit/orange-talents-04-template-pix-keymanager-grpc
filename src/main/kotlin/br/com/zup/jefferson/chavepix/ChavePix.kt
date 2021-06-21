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
}

@Embeddable
class Conta(
    val instituicao: String,
    val agencia: String,
    val numeroDaConta: String,
    val nomeTitular: String,
    val cpfTitular: String
) {}
