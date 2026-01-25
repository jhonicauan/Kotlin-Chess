package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

class Bishop(color: PieceColor) : Piece(color) {
    override val type = PieceType.BISHOP
    override fun clone(): Piece {
        return Bishop(color)
    }

    override val moveDirections = listOf<MoveVectorDTO>(
        MoveVectorDTO(1,1), MoveVectorDTO(1,-1),
        MoveVectorDTO(-1,1), MoveVectorDTO(-1,-1))

    override fun toString(): String {
        return if (color == PieceColor.WHITE) "♗" else "♝"
    }
}