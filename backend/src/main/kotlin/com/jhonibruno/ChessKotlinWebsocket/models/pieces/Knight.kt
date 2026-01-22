package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

class Knight(color: PieceColor) : Piece(color) {
    override val pieceType = PieceType.KNIGHT
    override val moveDirections = listOf<MoveVectorDTO>(
        MoveVectorDTO(2,1), MoveVectorDTO(2,-1),
        MoveVectorDTO(-2,1), MoveVectorDTO(-2,1),
        MoveVectorDTO(1,2), MoveVectorDTO(1,-2),
        MoveVectorDTO(-1,2), MoveVectorDTO(-1,-2))

    override fun getPossibleMoves(initialSlot: Slot, board: MutableList<MutableList<Slot>>): List<Move> {
        val possibleMoves = mutableListOf<Move>()
        for (direction in moveDirections) {
            val checkRow = initialSlot.row + direction.row
            val checkColumn = initialSlot.column + direction.column
            if (checkRow !in 0..7 || checkColumn !in 0..7) continue
            val destinationSlot = board[checkRow][checkColumn]
            val targetPiece = destinationSlot.piece
            val isCapture = targetPiece != null
            if (isCapture && checkColorMatches(targetPiece.color)) continue
            possibleMoves.add(Move(initialSlot,destinationSlot,isCapture))
        }
        return possibleMoves
    }

    override fun toString(): String {
        return if (color == PieceColor.WHITE) "♘" else "♞"
    }
}