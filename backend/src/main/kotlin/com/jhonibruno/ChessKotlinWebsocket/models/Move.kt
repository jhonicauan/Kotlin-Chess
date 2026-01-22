package com.jhonibruno.ChessKotlinWebsocket.models

import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot

data class Move(val pieceSlot: Slot,val destinationSlot: Slot,val isCapture: Boolean) {
    fun clone(): Move {
        return Move(pieceSlot.clone(), destinationSlot.clone(), isCapture)
    }
}
