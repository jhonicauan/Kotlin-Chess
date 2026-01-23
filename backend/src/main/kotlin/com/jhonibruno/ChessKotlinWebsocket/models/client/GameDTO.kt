package com.jhonibruno.ChessKotlinWebsocket.models.client

import com.jhonibruno.ChessKotlinWebsocket.models.enums.GameStatus
import com.jhonibruno.ChessKotlinWebsocket.models.history.MoveLogRegistry

data class GameDTO(val board: List<ClientBoardDTO>, val moveLog: List<MoveLogDTO>, val status: GameStatusDTO)
