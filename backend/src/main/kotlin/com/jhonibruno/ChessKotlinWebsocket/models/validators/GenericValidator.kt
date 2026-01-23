package com.jhonibruno.ChessKotlinWebsocket.models.validators

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot

class GenericValidator {
    companion object {
        fun getPossibleMoves(pieceSlot: Slot, boardSlots: List<List<Slot>>): List<Move> {
            val possibleMoves = mutableListOf<Move>()
            val piece = pieceSlot.piece ?: return possibleMoves

            val moveDirections = piece.moveDirections

            for (move in moveDirections) {
                var checkRow = pieceSlot.row
                var checkColumn = pieceSlot.column
                while (true) {

                    checkRow += move.row
                    checkColumn += move.column
                    if (checkRow > 7 || checkColumn > 7 || checkColumn < 0 || checkRow < 0) break

                    val destinationSlot = boardSlots[checkRow][checkColumn]
                    val targetPiece = destinationSlot.piece
                    val isCapture = targetPiece != null

                    if (isCapture && targetPiece.checkColorMatches(piece.color)) break
                    possibleMoves.add(Move(pieceSlot, destinationSlot, isCapture))
                    if (isCapture) break
                }
            }

            return possibleMoves
        }
    }
}