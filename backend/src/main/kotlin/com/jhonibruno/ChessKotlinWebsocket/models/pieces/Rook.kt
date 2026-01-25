package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

class Rook(color: PieceColor,    var isMoved: Boolean = false) : Piece(color) {
    override val type = PieceType.ROOK
    override fun clone(): Piece {
        return Rook(color)
    }
    override val moveDirections = listOf<MoveVectorDTO>(
        MoveVectorDTO(1,0), MoveVectorDTO(0, 1),
        MoveVectorDTO(-1, 0), MoveVectorDTO(0, -1)
    )

    override fun toString(): String {
        return if (color == PieceColor.WHITE) "♖" else "♜"
    }
}