package com.jhonibruno.ChessKotlinWebsocket.models.validators

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot

class KnightValidator {
    companion object {
        fun getPossibleMoves(pieceSlot: Slot, boardSlots: List<List<Slot>>): List<Move> {
            val possibleMoves = mutableListOf<Move>()
            val piece = pieceSlot.piece ?: return possibleMoves

            val moveDirections = piece.moveDirections

            for (move in moveDirections) {
                val checkRow = pieceSlot.row + move.row
                val checkColumn = pieceSlot.column + move.column
                if (checkRow > 7 || checkColumn > 7 || checkColumn < 0 || checkRow < 0) continue
                val destinationSlot = boardSlots[checkRow][checkColumn]
                val targetPiece = destinationSlot.piece
                val isCapture = targetPiece != null
                possibleMoves.add(Move(pieceSlot, destinationSlot, isCapture))
            }
            return possibleMoves
        }
    }
}