package com.jhonibruno.ChessKotlinWebsocket.models.client

data class MoveLogDTO(val round: Int, val notation: String,val roundBoard: List<ClientBoardDTO>, val piecePosition: String, val destinyPosition: String)
