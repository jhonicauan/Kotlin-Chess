package com.jhonibruno.ChessKotlinWebsocket.models.board

import com.jhonibruno.ChessKotlinWebsocket.models.client.ChessPieceDTO
import com.jhonibruno.ChessKotlinWebsocket.models.client.ClientBoardDTO
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Piece

data class Slot(val row: Int, val column: Int, var piece: Piece?) {
    fun clone(): Slot {
        return Slot(row,column,piece?.clone())
    }

    fun toDto(): ClientBoardDTO {
        val piece = piece?.let { ChessPieceDTO(it.color, it.type) }
        return ClientBoardDTO(toPosition(), piece)
    }

    fun toPosition(): String {
        val columns = listOf('a','b','c','d','e','f','g','h')
        val position: StringBuilder = StringBuilder("")
        position.append(columns[column])
        position.append(row+1)
        return position.toString()
    }
}
