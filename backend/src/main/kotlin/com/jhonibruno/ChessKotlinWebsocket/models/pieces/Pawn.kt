package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

class Pawn(color: PieceColor, val isMoved: Boolean = false): Piece(color) {
    override val moveDirections = listOf<MoveVectorDTO>(
        MoveVectorDTO(1,0), MoveVectorDTO(2,0),
        MoveVectorDTO(1,1), MoveVectorDTO(1,-1))

    override val pieceType = PieceType.PAWN

    override fun clone(): Piece {
        return Pawn(color, isMoved)
    }

    override fun toString(): String {
        return if (color == PieceColor.WHITE) "♙" else "♟"
    }
}