package com.jhonibruno.ChessKotlinWebsocket.models.history

import com.jhonibruno.ChessKotlinWebsocket.models.client.MoveLogDTO
import com.jhonibruno.ChessKotlinWebsocket.models.notation.MoveNotation

data class MoveLogRegistry(val round: Int,val notation: MoveNotation) {
    override fun toString(): String {
        return "$round -> $notation"
    }

    fun toDto(): MoveLogDTO {
        return MoveLogDTO(round, notation.toString())
    }
}
