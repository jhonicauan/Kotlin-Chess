package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

class Pawn(color: PieceColor, var isMoved: Boolean = false): Piece(color) {
    override val moveDirections: List<MoveVectorDTO> = listOf(
        MoveVectorDTO(1,0), MoveVectorDTO(2,0),
        MoveVectorDTO(1,1), MoveVectorDTO(1,-1)
    )

    override val type = PieceType.PAWN

    override fun clone(): Piece {
        return Pawn(color, isMoved)
    }

    override fun toString(): String {
        return if (color == PieceColor.WHITE) "♙" else "♟"
    }
}