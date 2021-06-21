package br.com.zup.jefferson.chavepix

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoDeChaveTest{

    @Nested
    inner class CPF{

        @Test
        fun `deveria retornar cpf quando for valido `(){
            val chaveAleatoria = TipoDeChave.CPF
            assertTrue(chaveAleatoria.validaChave("77395575016"))
        }

        @Test
        fun `deveria validar cpf quando for nulo ou vazio`(){
            val chaveAleatoria = TipoDeChave.CPF
            assertFalse(chaveAleatoria.validaChave(""))
            assertFalse(chaveAleatoria.validaChave(null))
        }

        @Test
        fun `deveria validar cpf quando possuir letras`(){
            val chaveAleatoria = TipoDeChave.CPF
            assertFalse(chaveAleatoria.validaChave("77395575016Shiranui"))
        }

        @Test
        fun `deveria validar cpf quando for invalido`(){
            val chaveAleatoria = TipoDeChave.CPF
            assertFalse(chaveAleatoria.validaChave("77395575016712"))
        }

    }

    @Nested
    inner class EMAIL{

        @Test
        fun `deveria retorna email quando for bem formatado`(){
            val chaveAleatoria = TipoDeChave.EMAIL
            assertTrue(chaveAleatoria.validaChave("rodrigo@gmail.com"))
        }

        @Test
        fun `deveria validar email quando for vazio ou nulo`(){
            val chaveAleatoria = TipoDeChave.EMAIL
            assertFalse(chaveAleatoria.validaChave(""))
            assertFalse(chaveAleatoria.validaChave(null))
        }

        @Test
        fun `deveria validar email quando ele nao for bem formatado`(){
            val chaveAleatoria = TipoDeChave.EMAIL
            assertFalse(chaveAleatoria.validaChave("rodrigogmail.com"))
        }
    }


    @Nested
    inner class ALEATORIA{
        @Test
        fun `deveria validar chave aleatoria quando o aleatorio for nulo ou vazio`(){
            val chaveAleatoria = TipoDeChave.CHAVE_ALEATORIA
            assertTrue(chaveAleatoria.validaChave(null))
            assertTrue(chaveAleatoria.validaChave(""))
        }

        @Test
        fun `deveria retornar falso quando  a chave aleatorio tiver valor `(){
            val chaveAleatoria = TipoDeChave.CHAVE_ALEATORIA
            assertFalse(chaveAleatoria.validaChave("Kujo Jotaro"))
        }
    }

    @Nested
    inner class NUMERO{
        @Test
        fun `deveria retornar true quando a mascara do numero for valida`(){
            val chaveAleatoria = TipoDeChave.NUMERO_CELULAR
            assertTrue(chaveAleatoria.validaChave("+5585988714077"))
        }

        @Test
        fun `deveria retornar false quando a mascara do numero for invalida`(){
            val chaveAleatoria = TipoDeChave.NUMERO_CELULAR
            assertFalse(chaveAleatoria.validaChave("5585988714077"))
        }

        @Test
        fun `deveria retornar false quando o numero for vazio ou nulo`(){
            val chaveAleatoria = TipoDeChave.NUMERO_CELULAR
            assertTrue(chaveAleatoria.validaChave("+5585988714077"))
        }
    }

}