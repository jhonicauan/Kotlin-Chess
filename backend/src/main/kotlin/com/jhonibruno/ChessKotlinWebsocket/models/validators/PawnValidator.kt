package com.jhonibruno.ChessKotlinWebsocket.models.validators

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Pawn

class PawnValidator {
    companion object {
        fun getPossibleMoves(pieceSlot: Slot, board: Board): List<Move> {
            val possibleMoves = mutableListOf<Move>()
            val pawn = pieceSlot.piece as? Pawn ?: return possibleMoves
            val moveDirections = pawn.moveDirections
            val slots = board.getSlots()
            for (move in moveDirections) {
                if (move.row == 2 && pawn.isMoved) continue
                val row = if(pawn.color == PieceColor.BLACK) move.row * -1 else move.row
                val checkRow = pieceSlot.row + row
                val checkColumn = pieceSlot.column + move.column
                if (checkRow > 7 || checkColumn > 7 || checkColumn < 0 || checkRow < 0) continue

                val destinationSlot = slots[checkRow][checkColumn]
                val targetPiece = destinationSlot.piece
                val isCapture = targetPiece != null

                if (isCapture && targetPiece.checkColorMatches(pawn.color)) continue
                if (isCapture && move.column == 0) continue
                if (!isCapture && move.column != 0) continue

                possibleMoves.add(Move(pieceSlot, destinationSlot, isCapture))
            }
            return possibleMoves
        }
    }
}