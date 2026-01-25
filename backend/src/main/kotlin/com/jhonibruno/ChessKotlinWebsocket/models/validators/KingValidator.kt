package com.jhonibruno.ChessKotlinWebsocket.models.validators

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.King
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Pawn
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Rook

class KingValidator {
    companion object {
        fun getPossibleMoves(pieceSlot: Slot, board: Board): List<Move> {
            val possibleMoves = mutableListOf<Move>()
            val king = pieceSlot.piece as? King ?: return possibleMoves
            val moveDirections = king.moveDirections
            val slots = board.getSlots()
            for (move in moveDirections) {
                val checkRow = pieceSlot.row + move.row
                val checkColumn = pieceSlot.column + move.column
                if (checkRow > 7 || checkColumn > 7 || checkColumn < 0 || checkRow < 0) continue

                val destinationSlot = slots[checkRow][checkColumn]
                val targetPiece = destinationSlot.piece
                val isCapture = targetPiece != null
                if (isCapture && targetPiece.checkColorMatches(king.color)) continue
                possibleMoves.add(Move(pieceSlot, destinationSlot, isCapture))
                }
            //if (board.canKingSideCastling(king.color)) possibleMoves.add(Move(pieceSlot,slots[pieceSlot.row][6],false))
            //if (board.canQueenSideCastling(king.color)) possibleMoves.add(Move(pieceSlot,slots[pieceSlot.row][2],false))
            return possibleMoves
        }
    }
}