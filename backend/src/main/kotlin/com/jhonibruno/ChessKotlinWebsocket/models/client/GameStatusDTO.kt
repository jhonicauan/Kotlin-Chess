package com.jhonibruno.ChessKotlinWebsocket.models.client

import com.jhonibruno.ChessKotlinWebsocket.models.enums.GameStatus
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor

data class GameStatusDTO(val isCheck: Boolean, val status: GameStatus, val actualColor: PieceColor)
