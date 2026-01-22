package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

abstract class Piece(val color: PieceColor) {
    abstract val moveDirections: List<MoveVectorDTO>
    abstract val pieceType: PieceType
    abstract fun getPossibleMoves(initialSlot: Slot, board: MutableList<MutableList<Slot>>): List<Move>
    fun checkColorMatches(color: PieceColor): Boolean {
        return color == this.color
    }
}
