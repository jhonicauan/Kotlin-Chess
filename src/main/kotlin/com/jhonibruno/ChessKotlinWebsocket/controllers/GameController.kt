package com.jhonibruno.ChessKotlinWebsocket.controllers

import com.jhonibruno.ChessKotlinWebsocket.services.GameService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/game")
class GameController(val gameService: GameService) {
    @GetMapping
    fun showBoard() {
        gameService.showBoard()
    }
}