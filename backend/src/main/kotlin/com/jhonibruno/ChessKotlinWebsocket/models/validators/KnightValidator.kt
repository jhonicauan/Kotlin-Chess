package com.jhonibruno.ChessKotlinWebsocket.models.validators

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot

class KnightValidator {
    companion object {
        fun getPossibleMoves(pieceSlot: Slot, board: Board): List<Move> {
            val possibleMoves = mutableListOf<Move>()
            val piece = pieceSlot.piece ?: return possibleMoves
            val slots = board.getSlots()
            val moveDirections = piece.moveDirections

            for (move in moveDirections) {
                val checkRow = pieceSlot.row + move.row
                val checkColumn = pieceSlot.column + move.column
                if (checkRow > 7 || checkColumn > 7 || checkColumn < 0 || checkRow < 0) continue
                val destinationSlot = slots[checkRow][checkColumn]
                val targetPiece = destinationSlot.piece
                val isCapture = targetPiece != null
                if (isCapture && destinationSlot.piece?.color == piece.color) continue
                possibleMoves.add(Move(pieceSlot, destinationSlot, isCapture))
            }
            return possibleMoves
        }
    }
}