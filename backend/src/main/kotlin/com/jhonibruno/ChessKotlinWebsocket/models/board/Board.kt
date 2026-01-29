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

    private companion object {
        const val WHITE_KINGS_ROW = 0
        const val BLACK_KINGS_ROW = 7

        const val QUEEN_SIDE_ROOK_COLUMN = 0
        const val QUEEN_SIDE_KNIGHT_COLUMN = 1
        const val QUEEN_SIDE_BISHOP_COLUMN = 2
        const val QUEEN_COLUMN = 3
        const val KING_COLUMN = 4
        const val KING_SIDE_BISHOP_COLUMN = 5
        const val KING_SIDE_KNIGHT_COLUMN = 6
        const val KING_SIDE_ROOK_COLUMN = 7
    }

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

    private fun initPiecePositions() {
        setPiecesInitialPositionsByColor(PieceColor.WHITE)
        setPiecesInitialPositionsByColor(PieceColor.BLACK)
    }

    private fun setPiecesInitialPositionsByColor(color: PieceColor) {
        val kingsRow = getKingsRowByColor(color)
        kingsRow[QUEEN_SIDE_ROOK_COLUMN].piece = Rook(color)
        kingsRow[QUEEN_SIDE_KNIGHT_COLUMN].piece = Knight(color)
        kingsRow[QUEEN_SIDE_BISHOP_COLUMN].piece = Bishop(color)
        kingsRow[QUEEN_COLUMN].piece = Queen(color)
        kingsRow[KING_COLUMN].piece = King(color)
        kingsRow[KING_SIDE_BISHOP_COLUMN].piece = Bishop(color)
        kingsRow[KING_SIDE_KNIGHT_COLUMN].piece = Knight(color)
        kingsRow[KING_SIDE_ROOK_COLUMN].piece = Rook(color)

        val colorsPawnRow = if (color == PieceColor.WHITE) slots[WHITE_KINGS_ROW + 1] else slots[BLACK_KINGS_ROW - 1]
        for (i in 0..7) {
            colorsPawnRow[i].piece = Pawn(color)
        }
    }

    fun getKingsRowByColor(color: PieceColor): MutableList<Slot> {
        return if (color == PieceColor.WHITE) slots[WHITE_KINGS_ROW] else slots[BLACK_KINGS_ROW]
    }

     fun getSlotByPosition(position: String): Slot {
        val col = columns.indexOf(position[0])
        val row = position[1].digitToInt() - 1
        return slots[row][col]
    }

    fun getSlots(): MutableList<MutableList<Slot>> {
        return slots
    }

    fun getCurrentState(): List<List<Slot>> {
        return slots.map { it.map { slot -> slot.clone() } }
    }

    private fun getKingSlot(color: PieceColor): Slot {
        return slots.flatten().first { it.piece?.color == color && it.piece?.type == PieceType.KING }
    }

    private fun getSlotsByColor(color: PieceColor): List<Slot> {
        return slots.flatten().filter { it.piece?.color == color }
    }

    fun movePiece(move: Move) {
        val pieceSlot = move.pieceSlot
        val destinationSlot = move.destinationSlot
        slots[destinationSlot.row][destinationSlot.column].piece = slots[pieceSlot.row][pieceSlot.column].piece
        val piece = slots[destinationSlot.row][destinationSlot.column].piece ?: return

        when (piece.type) {
            PieceType.PAWN -> (piece as Pawn).isMoved = true
            PieceType.KING -> (piece as King).isMoved = true
            PieceType.ROOK -> (piece as Rook).isMoved = true
            else -> {}
        }

        slots[pieceSlot.row][pieceSlot.column].piece = null
    }

     fun canPromote(destination: Slot,piece: Piece): Boolean {
        val promotionRow = if(piece.color == PieceColor.WHITE) BLACK_KINGS_ROW else WHITE_KINGS_ROW
        return piece.type == PieceType.PAWN && promotionRow == destination.row
    }

    fun promotePawn(destination: Slot, type: PieceType){
        if(destination.piece == null) throw IllegalArgumentException("Não se pode promover casas nulas")
        val color = destination.piece?.color ?: throw IllegalArgumentException("Cor invalida")

        destination.piece = when(type) {
            PieceType.KNIGHT -> Knight(color)
            PieceType.BISHOP -> Bishop(color)
            PieceType.ROOK -> Rook(color)
            PieceType.QUEEN -> Queen(color)
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
        val pieceType = piece.type

        return when (pieceType) {
            PieceType.KING -> KingValidator.getPossibleMoves(pieceSlot, this)
            PieceType.KNIGHT -> KnightValidator.getPossibleMoves(pieceSlot, this)
            PieceType.PAWN -> PawnValidator.getPossibleMoves(pieceSlot, this)
            else -> GenericValidator.getPossibleMoves(pieceSlot, this)
        }
    }

    fun getLegalMoves(pieceSlot: Slot): List<Move> {
        val legalMoves = getPossibleMoves(pieceSlot)
            .filter { isMoveLegal(it) }
            .toMutableList()

        val piece = pieceSlot.piece
        if (piece?.type == PieceType.KING) {
            if (canKingSideCastling(piece.color)) {
                legalMoves.add(Move(pieceSlot, slots[pieceSlot.row][KING_SIDE_KNIGHT_COLUMN], false))
            }
            if (canQueenSideCastling(piece.color)) {
                legalMoves.add(Move(pieceSlot, slots[pieceSlot.row][QUEEN_SIDE_KNIGHT_COLUMN], false))
            }
        }

        // TODO: En Passant
        //  se a jogada foi um peão que andou duas casas, guardar em uma propriedade (ex: enPassantPawn)
        //  verifica se a peça movida é um peão a uma distância de (1|-1,0)
        //  caso a distância em X seja 1 (peão movido está a direita)
        //    add captura em x-=1, y-+=1 (para frente)
        //  caso a distância em X seja -1 (peão movido está a esquerda)
        //    add captura em x+=1, y-+=1 (para frente)
        //  criar a "captura fantasma", pois o peão capturado não estará no slot de destino

        return legalMoves
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
            .filter {
                it != pieceSlot
                        && it.piece?.color == pieceSlot.piece?.color
                        && it.piece?.type == pieceSlot.piece?.type
                        && (it.row == pieceSlot.row || it.column == pieceSlot.column)
                        && getLegalMoves(it).contains(Move(it,destinationSlot,isCapture))
            }
    }

    private fun kingSideIsSafe(color: PieceColor): Boolean {
        val kingsRow = getKingsRowByColor(color)
        return verifySlotIsSafe(color, kingsRow[KING_SIDE_BISHOP_COLUMN]) && verifySlotIsSafe(color, kingsRow[KING_SIDE_KNIGHT_COLUMN])
    }

    private fun kingSideIsFree(color: PieceColor): Boolean {
        val kingsRow = getKingsRowByColor(color)
        return kingsRow[KING_SIDE_BISHOP_COLUMN].piece == null && kingsRow[KING_SIDE_KNIGHT_COLUMN].piece == null
    }

    private fun kingSideStillInThePlace(color: PieceColor): Boolean {
        val pieceInRooksInitialSlot = getKingsRowByColor(color)[KING_SIDE_ROOK_COLUMN].piece
        return kingAndRookStillInPlace(pieceInRooksInitialSlot)
    }

    fun canKingSideCastling(color: PieceColor): Boolean {
        return kingSideStillInThePlace(color) && kingSideIsFree(color) && kingSideIsSafe(color) && !isKingInCheck(color)
    }

    fun kingSideCastling(color: PieceColor) {
        val kingsRow = getKingsRowByColor(color)

        val king = kingsRow[KING_COLUMN].piece
        kingsRow[KING_SIDE_KNIGHT_COLUMN].piece = king
        kingsRow[KING_COLUMN].piece = null
        (king as King).isMoved = true

        val rook = kingsRow[KING_SIDE_ROOK_COLUMN].piece
        kingsRow[KING_SIDE_BISHOP_COLUMN].piece = rook
        kingsRow[KING_SIDE_ROOK_COLUMN].piece = null
        (rook as Rook).isMoved = true
    }

    private fun queenSideIsSafe(color: PieceColor): Boolean {
        val kingsRow = getKingsRowByColor(color)
        return verifySlotIsSafe(color, kingsRow[QUEEN_COLUMN]) && verifySlotIsSafe(color, kingsRow[QUEEN_SIDE_BISHOP_COLUMN])
    }

    private fun queenSideIsFree(color: PieceColor): Boolean {
        val kingsRow = getKingsRowByColor(color)
        return kingsRow[QUEEN_COLUMN].piece == null && kingsRow[QUEEN_SIDE_BISHOP_COLUMN].piece == null && kingsRow[QUEEN_SIDE_KNIGHT_COLUMN].piece == null
    }

    private fun queenSideStillInThePlace(color: PieceColor): Boolean {
        val pieceInRooksInitialSlot = getKingsRowByColor(color)[QUEEN_SIDE_ROOK_COLUMN].piece
        return kingAndRookStillInPlace(pieceInRooksInitialSlot)
    }

    private fun kingAndRookStillInPlace(pieceInRooksInitialSlot: Piece?): Boolean {
        if (pieceInRooksInitialSlot == null) return false
        if (pieceInRooksInitialSlot.type != PieceType.ROOK) return false

        val color = pieceInRooksInitialSlot.color
        val pieceInKingsInitialSlot = getKingsRowByColor(color)[KING_COLUMN].piece ?: return false

        if (pieceInKingsInitialSlot.type != PieceType.KING) return false

        val narrowedKing = pieceInKingsInitialSlot as King
        val narrowedRook = pieceInRooksInitialSlot as Rook
        return !narrowedKing.isMoved && !narrowedRook.isMoved
    }

    fun canQueenSideCastling(color: PieceColor): Boolean {
        return queenSideStillInThePlace(color) && queenSideIsFree(color) && queenSideIsSafe(color) && !isKingInCheck(color)
    }

    fun queenSideCastling(color: PieceColor) {
        val kingsRow = getKingsRowByColor(color)

        val king = kingsRow[KING_COLUMN].piece
        kingsRow[QUEEN_SIDE_KNIGHT_COLUMN].piece = king
        kingsRow[KING_COLUMN].piece = null
        (king as King).isMoved = true

        val rook = kingsRow[QUEEN_SIDE_ROOK_COLUMN].piece
        kingsRow[QUEEN_COLUMN].piece = rook
        kingsRow[QUEEN_SIDE_ROOK_COLUMN].piece = null
        (rook as Rook).isMoved = true
    }

    init {
        generateBoard()
        initPiecePositions()
    }
}