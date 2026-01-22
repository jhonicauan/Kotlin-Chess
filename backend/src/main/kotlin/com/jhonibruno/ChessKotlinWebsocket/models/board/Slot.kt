package com.jhonibruno.ChessKotlinWebsocket.models.board

import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Piece

data class Slot(val row: Int, val column: Int, var piece: Piece?)
