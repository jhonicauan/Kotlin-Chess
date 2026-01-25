package com.jhonibruno.ChessKotlinWebsocket.models.history

import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.client.MoveLogDTO
import com.jhonibruno.ChessKotlinWebsocket.models.notation.MoveNotation

data class MoveLogRegistry(val round: Int,val notation: MoveNotation, val boardSlots: List<Slot>) {
    override fun toString(): String {
        return "$round -> $notation"
    }

    fun toDto(): MoveLogDTO {
        return MoveLogDTO(round, notation.toString(),boardSlots.map { it.toDto() },notation.move.pieceSlot.toPosition(),notation.move.destinationSlot.toPosition())
    }
}
