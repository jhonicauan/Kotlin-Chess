package com.jhonibruno.ChessKotlinWebsocket.models

import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot

data class Move(val initialSlot: Slot,val destinationSlot: Slot,val isCapture: Boolean)
