package com.jhonibruno.ChessKotlinWebsocket.models.board

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.*
import com.jhonibruno.ChessKotlinWebsocket.models.validators.GenericValidator
import com.jhonibruno.ChessKotlinWebsocket.models.validators.KingValidator
import com.jhonibruno.ChessKotlinWebsocket.models.validators.KnightValidator
import com.jhonibruno.ChessKotlinWebsocket.models.validators.PawnValidator

class Board {
    private val slots: MutableList<MutableList<Slot>> = mutableListOf()
    private val columns: List<Char> = listOf('a','b','c','d','e','f','g','h')
    private fun generateBoard() {
        if (!slots.isEmpty()) return

        for (i in 0..7) {
            val row = mutableListOf<Slot>()
            for (j in 0..7) {
                row.add(Slot(i, j, null))
            }
            slots.add(row)
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

    fun getSlots(): MutableList<MutableList<Slot>> {
        return slots
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
        val piece = slots[destinationSlot.row][destinationSlot.column].piece ?: return

        if(piece?.pieceType == PieceType.PAWN) (piece as Pawn).isMoved = true
        if(piece?.pieceType == PieceType.KING) (piece as King).isMoved = true
        if(piece?.pieceType == PieceType.ROOK) (piece as Rook).isMoved = true
        slots[pieceSlot.row][pieceSlot.column].piece = null
    }

     fun canPromote(destination: Slot,piece: Piece): Boolean {
        val promotionRow = if(piece.color == PieceColor.WHITE)  7 else 0
        return piece.pieceType == PieceType.PAWN && promotionRow == destination.row
    }

    fun promotePawn(destination: Slot, type: PieceType){
        if(destination.piece == null) throw IllegalArgumentException("Não se pode promover casas nulas")
        val color = destination.piece?.color ?: throw IllegalArgumentException("Cor invalida")
        when(type){
            PieceType.KNIGHT -> destination.piece = Knight(color)
            PieceType.BISHOP -> destination.piece = Bishop(color)
            PieceType.ROOK -> destination.piece = Rook(color)
            PieceType.QUEEN -> destination.piece = Queen(color)
            else -> throw IllegalArgumentException("PROMOÇÃO INVALIDA")
        }
    }

    private fun reverseMove(move: Move) {
        val pieceSlot = move.pieceSlot
        val destinationSlot = move.destinationSlot
        slots[destinationSlot.row][destinationSlot.column].piece = destinationSlot.piece
        slots[pieceSlot.row][pieceSlot.column].piece = pieceSlot.piece
    }

    private fun isMoveLegal(move: Move): Boolean {
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
            PieceType.KING -> KingValidator.getPossibleMoves(pieceSlot, this)
            PieceType.KNIGHT -> KnightValidator.getPossibleMoves(pieceSlot, this)
            PieceType.PAWN -> PawnValidator.getPossibleMoves(pieceSlot, this)
            else -> GenericValidator.getPossibleMoves(pieceSlot, this)
        }
    }

    fun getLegalMoves(pieceSlot: Slot): List<Move> {
        return getPossibleMoves(pieceSlot).filter { isMoveLegal(it) }
    }

    fun existsSafeMoves(color: PieceColor): Boolean {
        val validMoves = mutableListOf<Move>()
        val slotsByColor = getSlotsByColor(color)
        for (slot in slotsByColor) {
            val possibleMoves = getLegalMoves(slot)
            validMoves.addAll(possibleMoves)
        }
        return !validMoves.isEmpty()
    }

    private fun verifySlotIsSafe(color: PieceColor,slot: Slot): Boolean {
        val enemyColor = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        val enemiesSlots = getSlotsByColor(enemyColor)
        for (enemySlot in enemiesSlots) {
            if (getPossibleMoves(enemySlot).any{it.destinationSlot == slot}) return false
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

    fun getAlternativePiece(move: Move): List<Slot> {
        val pieceSlot = move.pieceSlot
        val destinationSlot = move.destinationSlot
        val isCapture = move.isCapture
        return slots.flatten()
            .filter { it != pieceSlot
                    && it.piece?.color == pieceSlot.piece?.color
                    && it.piece?.pieceType == pieceSlot.piece?.pieceType
                    && (it.row == pieceSlot.row || it.column == pieceSlot.column)
                    && getLegalMoves(it).contains(Move(it,destinationSlot,isCapture))}
    }

    private fun kingSideIsSafe(color: PieceColor): Boolean {
        if (color == PieceColor.WHITE) return verifySlotIsSafe(color,slots[0][5]) && verifySlotIsSafe(color,slots[0][6])
        return verifySlotIsSafe(color,slots[7][5]) && verifySlotIsSafe(color,slots[7][6])
    }

    private fun kingSideIsFree(color: PieceColor): Boolean {
        if (color == PieceColor.WHITE) return slots[0][5].piece == null && slots[0][6].piece == null
        return slots[7][5].piece == null && slots[7][6].piece == null
    }

    private fun kingSideStillInThePlace(color: PieceColor): Boolean {
        if (color == PieceColor.WHITE) {
            if (slots[0][4].piece == null || slots[0][7].piece == null) return false
            if (slots[0][4].piece?.pieceType != PieceType.KING || slots[0][7].piece?.pieceType != PieceType.ROOK) return false
            val king = slots[0][4].piece as King
            val rook = slots[0][7].piece as Rook
            return !king.isMoved && !rook.isMoved
        }
        if (slots[7][4].piece == null || slots[7][7].piece == null) return false
        if (slots[7][4].piece?.pieceType != PieceType.KING || slots[7][7].piece?.pieceType != PieceType.ROOK) return false
        val king = slots[7][4].piece as King
        val rook = slots[7][7].piece as Rook
        return !king.isMoved && !rook.isMoved
    }

    fun canKingSideCastling(color: PieceColor): Boolean {
        return kingSideStillInThePlace(color) && kingSideIsFree(color) && kingSideIsSafe(color) && !isKingInCheck(color)
    }

    fun kingSideCastling(color: PieceColor) {
        val colorRow = if (color == PieceColor.WHITE) 0 else 7
        slots[colorRow][4].piece = null
        slots[colorRow][7].piece = null
        slots[colorRow][6].piece = King(color,true)
        slots[colorRow][5].piece = Rook(color,true)
    }

    private fun queenSideIsSafe(color: PieceColor): Boolean {
        if (color == PieceColor.WHITE) return verifySlotIsSafe(color,slots[0][3]) && verifySlotIsSafe(color,slots[0][2])
        return verifySlotIsSafe(color,slots[7][3]) && verifySlotIsSafe(color,slots[7][2])
    }

    private fun queenSideIsFree(color: PieceColor): Boolean {
        if (color == PieceColor.WHITE) return slots[0][3].piece == null && slots[0][2].piece == null && slots[0][1].piece == null
        return slots[7][3].piece == null && slots[7][2].piece == null && slots[7][1].piece == null
    }

    private fun queenSideStillInThePlace(color: PieceColor): Boolean {
        if (color == PieceColor.WHITE) {
            if (slots[0][4].piece == null || slots[0][0].piece == null) return false
            if (slots[0][4].piece?.pieceType != PieceType.KING || slots[0][0].piece?.pieceType != PieceType.ROOK) return false
            val king = slots[0][4].piece as King
            val rook = slots[0][0].piece as Rook
            return !king.isMoved && !rook.isMoved
        }
        if (slots[7][4].piece == null || slots[7][0].piece == null) return false
        if (slots[7][4].piece?.pieceType != PieceType.KING && slots[7][0].piece?.pieceType != PieceType.ROOK) return false
        val king = slots[7][4].piece as King
        val rook = slots[7][0].piece as Rook
        return !king.isMoved && !rook.isMoved
    }

    fun canQueenSideCastling(color: PieceColor): Boolean {
        return queenSideStillInThePlace(color) && queenSideIsFree(color) && queenSideIsSafe(color) && !isKingInCheck(color)
    }

    fun queenSideCastling(color: PieceColor) {
        val colorRow = if (color == PieceColor.WHITE) 0 else 7
        slots[colorRow][4].piece = null
        slots[colorRow][0].piece = null
        slots[colorRow][2].piece = King(color,true)
        slots[colorRow][3].piece = Rook(color,true)
    }

    init {
        generateBoard()
        setPieces()
    }
}