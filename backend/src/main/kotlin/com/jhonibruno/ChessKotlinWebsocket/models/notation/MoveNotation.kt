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
    val notation: String

    fun generateNotation(): String {
        val notationBuilder = StringBuilder()
        appendCastlingKing(notationBuilder)
        appendCastlingQueen(notationBuilder)
        if (!isCastlingKing && !isCastlingQueen) {
            appendPiece(notationBuilder)
            appendIsCapture(notationBuilder)
            appendPosition(notationBuilder)
            appendPromotion(notationBuilder)
        }
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
        if (piecePromotedTo != null) notationBuilder.append("=${piecePromotedTo.letter}")
    }

    private fun appendPosition(notationBuilder: StringBuilder) {
        val columns = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
        move.destinationSlot.let {
            notationBuilder
                .append(columns[it.column])
                .append(it.row + 1)
        }
    }

    private fun appendIsCapture(notationBuilder: StringBuilder) {
        if (move.isCapture) notationBuilder.append('x')
    }

    private fun appendPiece(notationBuilder: StringBuilder) {
        val piece: Piece? = move.pieceSlot.piece
        if (piece?.pieceType != PieceType.PAWN) notationBuilder.append(piece?.pieceType?.letter)
        val columns = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
        var isColumnRepeated = false
        var isRowRepeated = false
        for (slot in alternativePieces) {
            if (slot.row == move.pieceSlot.row) isRowRepeated = true
            if (slot.column == move.pieceSlot.column) isColumnRepeated = true
        }
        if (isColumnRepeated) notationBuilder.append(move.pieceSlot.row + 1)
        if (isRowRepeated || (piece?.pieceType == PieceType.PAWN && move.isCapture)) notationBuilder.append(columns[move.pieceSlot.column])
    }

    override fun toString(): String {
        return notation
    }

    init {
        notation = generateNotation()
    }
}