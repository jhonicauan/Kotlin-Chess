package com.jhonibruno.ChessKotlinWebsocket.models.client

import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

data class ChessPieceDTO(val color: PieceColor,val pieceType: PieceType)
