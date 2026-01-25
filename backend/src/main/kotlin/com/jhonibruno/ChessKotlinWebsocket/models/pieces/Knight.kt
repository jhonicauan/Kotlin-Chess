package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

class Knight(color: PieceColor) : Piece(color) {
    override val type = PieceType.KNIGHT
    override fun clone(): Piece {
        return Knight(color)
    }

    override val moveDirections = listOf<MoveVectorDTO>(
        MoveVectorDTO(2, 1), MoveVectorDTO(2, -1),
        MoveVectorDTO(-2, 1), MoveVectorDTO(-2, -1),
        MoveVectorDTO(1, 2), MoveVectorDTO(1, -2),
        MoveVectorDTO(-1, 2), MoveVectorDTO(-1, -2)
    )
    override fun toString(): String {
        return if (color == PieceColor.WHITE) "♘" else "♞"
    }
}