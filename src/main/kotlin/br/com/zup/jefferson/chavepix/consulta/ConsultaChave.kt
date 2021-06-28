package br.com.zup.jefferson.chavepix.consulta

import br.com.zup.jefferson.chavepix.ChavePixResponse
import br.com.zup.jefferson.chavepix.PixRepository
import br.com.zup.jefferson.sistemaexterno.BcbClient

interface ConsultaChave {

    fun consulta(pixRepository: PixRepository, bcbClient: BcbClient) : ChavePixResponse
}

