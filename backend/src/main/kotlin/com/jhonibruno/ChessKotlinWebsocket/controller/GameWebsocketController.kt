package com.jhonibruno.ChessKotlinWebsocket.controller

import com.jhonibruno.ChessKotlinWebsocket.models.ClientMoveDTO
import com.jhonibruno.ChessKotlinWebsocket.models.Game
import com.jhonibruno.ChessKotlinWebsocket.models.Move
import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.client.ClientBoardDTO
import com.jhonibruno.ChessKotlinWebsocket.models.client.GameDTO
import com.jhonibruno.ChessKotlinWebsocket.models.client.GameStatusDTO
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("/game")
class GameWebsocketController {

    val games: MutableMap<String, Game>  = mutableMapOf()

    @MessageMapping("/create")
    fun createGame(gameName: String) {
        if (games.containsKey(gameName)) return
        games[gameName] = Game(Board())
    }

    @MessageMapping("/joined/{gameName}")
    @SendTo("/topic/game/{gameName}")
    fun joined(@DestinationVariable gameName: String): GameDTO {
        val game = games[gameName] ?: throw IllegalArgumentException("Game $gameName not found")
        return game.getGameDTO()
    }

    @MessageMapping("/checkMoves/{gameName}")
    @SendToUser("/queue/check/{gameName}")
    fun checkMoves(@DestinationVariable gameName: String, move: String): List<ClientBoardDTO> {
        val game = games[gameName] ?: throw IllegalArgumentException("Game $gameName not found")
        return game.viewPossibleMoves(move)
    }

    @MessageMapping("/makeMove/{gameName}")
    @SendTo("/topic/game/{gameName}")
    fun makeMove(@DestinationVariable gameName: String, move: ClientMoveDTO): GameDTO {
        val game = games[gameName] ?: throw IllegalArgumentException("Game $gameName not found")
        game.makeMove(move)
        return game.getGameDTO()
    }


}