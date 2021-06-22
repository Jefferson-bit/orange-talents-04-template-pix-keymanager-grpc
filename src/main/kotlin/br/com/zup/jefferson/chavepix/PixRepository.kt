package br.com.zup.jefferson.chavepix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface PixRepository : JpaRepository<ChavePix, String> {

    fun existsByChavePix(chavePix: String) : Boolean

    fun findByIdClienteAndChavePix(idCliente: UUID?, chavePix: String?): Optional<ChavePix>
}