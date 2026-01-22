package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

class Rook(color: PieceColor) : Piece(color) {
    override val pieceType = PieceType.ROOK
    override val moveDirections = listOf<MoveVectorDTO>(
        MoveVectorDTO(1,0), MoveVectorDTO(0, 1),
        MoveVectorDTO(-1, 0), MoveVectorDTO(0, -1)
    )

    override fun getPossibleMoves(initialSlot: Slot, board: MutableList<MutableList<Slot>>): List<Move> {
        val possibleMoves = mutableListOf<Move>()
        for (direction in moveDirections) {
            var checkRow = initialSlot.row
            var checkColumn = initialSlot.column
            while (true) {
                checkRow += direction.row
                checkColumn += direction.column
                if (checkRow !in 0..7 || checkColumn !in 0..7) break
                val destinationSlot = board[checkRow][checkColumn]
                val targetPiece = destinationSlot.piece
                val isCapture = targetPiece != null

                if (isCapture && checkColorMatches(targetPiece.color)) break
                possibleMoves.add(Move(initialSlot,destinationSlot,isCapture))

                if (isCapture) break
            }
        }
        return possibleMoves
    }

    override fun toString(): String {
        return if (color == PieceColor.WHITE) "♖" else "♜"
    }
}