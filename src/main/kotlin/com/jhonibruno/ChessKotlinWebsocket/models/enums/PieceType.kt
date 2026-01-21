package com.jhonibruno.ChessKotlinWebsocket.models.enums

enum class PieceType(val letter: Char) {
    KING('K'),
    QUEEN('Q'),
    KNIGHT('N'),
    BISHOP('B'),
    ROOK('R'),
    PAWN('P');

    companion object {
        fun getByLetter(letter: Char): PieceType {
            return entries.firstOrNull{ it.letter == letter } ?: PAWN
        }
    }
}