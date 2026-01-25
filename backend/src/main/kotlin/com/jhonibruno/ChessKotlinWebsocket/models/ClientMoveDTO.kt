package com.jhonibruno.ChessKotlinWebsocket.models

import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType

data class ClientMoveDTO(val position: String,val destiny: String,val promotion: PieceType?)
