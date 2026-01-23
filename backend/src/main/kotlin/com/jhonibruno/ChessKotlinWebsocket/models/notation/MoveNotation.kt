package com.jhonibruno.ChessKotlinWebsocket.models.notation

import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Piece

class MoveNotation(
    private val move: Move,
    private val alternativePiece: List<Slot>,
    private val isCheck: Boolean,
    private val isCheckmate: Boolean,
    private val isCastlingKing: Boolean,
    private val isCastlingQueen: Boolean,
    private val piecePromotedTo: PieceType?
) {
    val notation: String

    fun generateNotation(): String {
        if (isCastlingKing) return "O-O"
        if (isCastlingQueen) return "O-O-O"

        val notationBuilder = StringBuilder()

        appendPiece(notationBuilder)
        appendIsCapture(notationBuilder)
        appendPosition(notationBuilder)
        appendPromotion(notationBuilder)
        appendIsCheck(notationBuilder)
        appendIsCheckmate(notationBuilder)

        return notationBuilder.toString()
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
    }

    init {
        notation = generateNotation()
    }
}