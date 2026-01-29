package com.jhonibruno.ChessKotlinWebsocket.models.notation

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Piece

class MoveNotation(
    val move: Move,
    private val alternativePieces: List<Slot>,
    private val isCheck: Boolean,
    private val isCheckmate: Boolean,
    private val isCastlingKing: Boolean,
    private val isCastlingQueen: Boolean,
    private val piecePromotedTo: PieceType?
) {
    private companion object {
        val COLUMN_NAMES = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    }

    val notation: String

    init {
        notation = generateNotation()
    }

    fun generateNotation(): String {
        val notationBuilder = StringBuilder()

        appendCastlingKing(notationBuilder)
        appendCastlingQueen(notationBuilder)
        appendPiece(notationBuilder)
        appendIsCapture(notationBuilder)
        appendPosition(notationBuilder)
        appendPromotion(notationBuilder)
        appendIsCheck(notationBuilder)
        appendIsCheckmate(notationBuilder)

        return notationBuilder.toString()
    }

    private fun appendCastlingKing(notationBuilder: StringBuilder) {
        if (isCastlingKing) notationBuilder.append("O-O")
    }

    private fun appendCastlingQueen(notationBuilder: StringBuilder) {
        if (isCastlingQueen) notationBuilder.append("O-O-O")
    }

    private fun appendIsCheckmate(notationBuilder: StringBuilder) {
        if (isCheckmate) notationBuilder.append('#')
    }

    private fun appendIsCheck(notationBuilder: StringBuilder) {
        if (isCheck && !isCheckmate) notationBuilder.append('+')
    }

    private fun appendPromotion(notationBuilder: StringBuilder) {
        if (isCastlingKing || isCastlingQueen) return
        if (piecePromotedTo != null) notationBuilder.append("=${piecePromotedTo.letter}")
    }

    private fun appendPosition(notationBuilder: StringBuilder) {
        if (isCastlingKing || isCastlingQueen) return

        move.destinationSlot.let {
            notationBuilder
                .append(COLUMN_NAMES[it.column])
                .append(it.row + 1)
        }
    }

    private fun appendIsCapture(notationBuilder: StringBuilder) {
        if (isCastlingKing || isCastlingQueen) return
        if (move.isCapture) notationBuilder.append('x')
    }

    private fun appendPiece(notationBuilder: StringBuilder) {
        if (isCastlingKing || isCastlingQueen) return

        val piece: Piece? = move.pieceSlot.piece
        if (piece?.type != PieceType.PAWN) notationBuilder.append(piece?.type?.letter)
        var isColumnRepeated = false
        var isRowRepeated = false
        for (slot in alternativePieces) {
            if (slot.row == move.pieceSlot.row) isRowRepeated = true
            if (slot.column == move.pieceSlot.column) isColumnRepeated = true
        }
        if (isColumnRepeated) notationBuilder.append(move.pieceSlot.row + 1)
        if (isRowRepeated || (piece?.type == PieceType.PAWN && move.isCapture)) notationBuilder.append(COLUMN_NAMES[move.pieceSlot.column])
    }

    override fun toString(): String {
        return notation
    }
}