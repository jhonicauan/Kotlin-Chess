package com.jhonibruno.ChessKotlinWebsocket.models.board

import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.*
import org.springframework.stereotype.Component

@Component
class Board {
    val slots: MutableList<MutableList<Slot>> = mutableListOf()

    private fun generateBoard() {
        for (i in 0..7) {
            val row = mutableListOf<Slot>()
            for (j in 0..7) {
                row.add(Slot(i, j, null))
            }
            slots.add(row)
        }
    }

    private fun setPieces() {
        val white = PieceColor.WHITE
        val black = PieceColor.BLACK
        slots[0][0].piece = Rook(white)
        slots[0][1].piece = Knight(white)
        slots[0][2].piece = Bishop(white)
        slots[0][3].piece = Queen(white)
        slots[0][4].piece = King(white)
        slots[0][5].piece = Bishop(white)
        slots[0][6].piece = Knight(white)
        slots[0][7].piece = Rook(white)

        slots[7][0].piece = Rook(black)
        slots[7][1].piece = Knight(black)
        slots[7][2].piece = Bishop(black)
        slots[7][3].piece = Queen(black)
        slots[7][4].piece = King(black)
        slots[7][5].piece = Bishop(black)
        slots[7][6].piece = Knight(black)
        slots[7][7].piece = Rook(black)

        for (i in 0..7) {
            slots[1][i].piece = Pawn(white)
            slots[6][i].piece = Pawn(black)
        }
    }


    fun showBoard() {
        for (i in 7 downTo 0) {
            for (j in 0..7) {
                print(slots[i][j].piece ?: '.')
            }
            println("")
        }
    }

    init {
        generateBoard()
        setPieces()
    }

}