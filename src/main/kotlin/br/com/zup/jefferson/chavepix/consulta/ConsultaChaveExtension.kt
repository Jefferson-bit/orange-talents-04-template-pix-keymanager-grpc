package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.ConsultaPixRequest
import br.com.zup.jefferson.ConsultaPixRequest.FiltroCase.PIXID
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun ConsultaPixRequest.toModel(validator: Validator): ConsultaChave {

    val consulta = when (filtroCase) {
        PIXID -> pixId.let {
            ConsultaChavePorPixId(pixId = it.pixId, clienteId = it.clienteId)
        }
        else -> ConsultaChavePorChavePix(chave = chavePix)
    }

        val violations =  validator.validate(consulta)
        if(violations.isNotEmpty())
            throw ConstraintViolationException(violations)
        return consulta
    }
