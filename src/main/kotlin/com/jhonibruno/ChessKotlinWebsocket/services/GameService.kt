package com.jhonibruno.ChessKotlinWebsocket.services

import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameService(private val board: Board) {
    fun showBoard() {
        board.showBoard()
    }
}