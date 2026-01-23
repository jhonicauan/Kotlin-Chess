package com.jhonibruno.ChessKotlinWebsocket.controller

import com.jhonibruno.ChessKotlinWebsocket.models.ClientMoveDTO
import com.jhonibruno.ChessKotlinWebsocket.models.Game
import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.client.ClientBoardDTO
import com.jhonibruno.ChessKotlinWebsocket.models.client.GameDTO
import com.jhonibruno.ChessKotlinWebsocket.models.client.GameStatusDTO
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("/game")
class GameWebsocketController {

    val game = Game(Board())

    @MessageMapping("/joined")
    @SendTo("/topic/game")
    fun joined(): GameDTO {
        return game.getGameDTO()
    }

    @MessageMapping("/checkMoves")
    @SendToUser("/queue/check")
    fun checkMoves(move: String): List<ClientBoardDTO> {
        return game.viewPossibleMoves(move)
    }

    @MessageMapping("/makeMove")
    @SendTo("/topic/game")
    fun makeMove(move: ClientMoveDTO): GameDTO {
        game.makeMove(move.position, move.destiny)
        return game.getGameDTO()
    }


}