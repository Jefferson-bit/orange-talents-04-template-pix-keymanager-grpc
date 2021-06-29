package br.com.zup.jefferson.chavepix

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface PixRepository : JpaRepository<ChavePix, String> {

    fun existsByChavePix(chavePix: String) : Boolean
    fun findByIdClienteAndPixId(idCliente: UUID?, pixId: String?): Optional<ChavePix>
    @Query("SELECT obj FROM ChavePix obj WHERE obj.idCliente = :idCliente")
    fun listAllChavePix(idCliente: UUID?) : List<ChavePix>
}