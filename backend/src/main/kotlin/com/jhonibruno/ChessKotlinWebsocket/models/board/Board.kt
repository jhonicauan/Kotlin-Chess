package com.jhonibruno.ChessKotlinWebsocket.models.board

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.*
import com.jhonibruno.ChessKotlinWebsocket.models.validators.GenericValidator
import com.jhonibruno.ChessKotlinWebsocket.models.validators.KingValidator
import com.jhonibruno.ChessKotlinWebsocket.models.validators.KnightValidator
import com.jhonibruno.ChessKotlinWebsocket.models.validators.PawnValidator

class Board(private val slots: MutableList<MutableList<Slot>>) {
    private val columns: List<Char> = listOf('a','b','c','d','e','f','g','h')

    private fun generateBoard() {
        if (slots.isEmpty()) {
            for (i in 0..7) {
                val row = mutableListOf<Slot>()
                for (j in 0..7) {
                    row.add(Slot(i, j, null))
                }
                slots.add(row)
            }
        }
    }

    private fun setPieces() {
        val white = PieceColor.WHITE
        val black = PieceColor.BLACK
        slots[0][0].piece = Rook(white)
        slots[0][1].piece = Knight(white)
        slots[0][2].piece = Bishop(white)
        slots[0][3].piece = Queen(white)
        slots[0][4].piece = King(white)
        slots[0][5].piece = Bishop(white)
        slots[0][6].piece = Knight(white)
        slots[0][7].piece = Rook(white)

        slots[7][0].piece = Rook(black)
        slots[7][1].piece = Knight(black)
        slots[7][2].piece = Bishop(black)
        slots[7][3].piece = Queen(black)
        slots[7][4].piece = King(black)
        slots[7][5].piece = Bishop(black)
        slots[7][6].piece = Knight(black)
        slots[7][7].piece = Rook(black)

        for (i in 0..7) {
            slots[1][i].piece = Pawn(white)
            slots[6][i].piece = Pawn(black)
        }
    }

     fun getSlotByPosition(position: String): Slot {
        val col = columns.indexOf(position[0])
        val row = position[1].digitToInt() - 1
        return slots[row][col]
    }

    private fun getKingSlot(color: PieceColor): Slot {
        return slots.flatten().first { it.piece?.color == color && it.piece?.pieceType == PieceType.KING }
    }

    private fun getSlotsByColor(color: PieceColor): List<Slot> {
        return slots.flatten().filter { it.piece?.color == color }
    }

    fun movePiece(move: Move) {
        val pieceSlot = move.pieceSlot
        val destinationSlot = move.destinationSlot
        slots[destinationSlot.row][destinationSlot.column].piece = slots[pieceSlot.row][pieceSlot.column].piece
        slots[pieceSlot.row][pieceSlot.column].piece = null
    }

    private fun reverseMove(move: Move) {
        val pieceSlot = move.pieceSlot
        val destinationSlot = move.destinationSlot
        slots[destinationSlot.row][destinationSlot.column].piece = destinationSlot.piece
        slots[pieceSlot.row][pieceSlot.column].piece = pieceSlot.piece
    }

    private fun isMoveValid(move: Move): Boolean {
        val piece = move.pieceSlot.piece ?: return false
        val actualColor = piece.color
        val backupMove = move.clone()
        movePiece(move)
        val valid = !isKingInCheck(actualColor)
        reverseMove(backupMove)
        return valid
    }

    private fun getPossibleMoves(pieceSlot: Slot): List<Move> {
        val piece = pieceSlot.piece ?: return listOf()
        val pieceType = piece.pieceType

        return when (pieceType) {
            PieceType.KING -> KingValidator.getPossibleMoves(pieceSlot, slots)
            PieceType.KNIGHT -> KnightValidator.getPossibleMoves(pieceSlot, slots)
            PieceType.PAWN -> PawnValidator.getPossibleMoves(pieceSlot, slots)
            else -> GenericValidator.getPossibleMoves(pieceSlot, slots)
        }
    }

    fun getLegalMoves(pieceSlot: Slot): List<Move> {
        return getPossibleMoves(pieceSlot).filter { isMoveValid(it) }
    }

    fun existsSafeMoves(color: PieceColor): Boolean {
        val validMoves = mutableListOf<Move>()
        val pieces = getSlotsByColor(color)
        for (piece in pieces) {
            val possibleMoves = getLegalMoves(piece)
            validMoves.addAll(possibleMoves)
        }
        return !validMoves.isEmpty()
    }

    private fun verifySlotIsSafe(color: PieceColor,slot: Slot): Boolean {
        val enemyColor = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        val enemiesSlots = getSlotsByColor(enemyColor)
        for (enemySlot in enemiesSlots) {
            val captureMove = Move(enemySlot, slot, true)
            if (getPossibleMoves(enemySlot).contains(captureMove)) return false
        }
        return true
    }

    fun isKingInCheck(color: PieceColor): Boolean {
        val kingSlot = getKingSlot(color)
        return !verifySlotIsSafe(color, kingSlot)
    }

    fun showBoard() {
        for (i in 7 downTo 0) {
            for (j in 0..7) {
                print(slots[i][j].piece ?: '.')
            }
            println("")
        }
    }

    init {
        generateBoard()
        setPieces()
    }

}