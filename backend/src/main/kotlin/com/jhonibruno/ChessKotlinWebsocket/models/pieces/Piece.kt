package com.jhonibruno.ChessKotlinWebsocket.models.pieces

import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

abstract class Piece(val color: PieceColor) {
    abstract val moveDirections: List<MoveVectorDTO>
    abstract val type: PieceType

    fun checkColorMatches(color: PieceColor): Boolean {
        return color == this.color
    }
    abstract fun clone(): Piece
}
