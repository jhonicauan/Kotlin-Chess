package com.jhonibruno.ChessKotlinWebsocket.models

import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.enums.GameStatus
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Piece

class Game(private val board: Board) {

    var actualPlayerColor: PieceColor = PieceColor.WHITE
    var status = GameStatus.STARTED

    fun changePlayerColor() {
        actualPlayerColor = if (actualPlayerColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
    }

    fun isKingInCheck(): Boolean {
        return board.isKingInCheck(actualPlayerColor) && board.existsSafeMoves(actualPlayerColor)
    }

    fun isKingInCheckmate(): Boolean {
        return board.isKingInCheck(actualPlayerColor) && !board.existsSafeMoves(actualPlayerColor)
    }

    fun isStalemate(): Boolean {
        return !board.isKingInCheck(actualPlayerColor) && !board.existsSafeMoves(actualPlayerColor)
    }

    fun checkStatus() {
        if (isKingInCheckmate() && actualPlayerColor == PieceColor.WHITE) status = GameStatus.BLACK_WINS
        if (isKingInCheckmate() && actualPlayerColor == PieceColor.BLACK) status = GameStatus.WHITE_WINS
        if (isStalemate()) status = GameStatus.STALEMATE
        if (status == GameStatus.BLACK_WINS) println("Vitoria das pretas")
        if (status == GameStatus.WHITE_WINS) println("Vitoria das brancas")
        if (status == GameStatus.STALEMATE) println("Empate")
    }

    private fun checkPossibleMove(move: Move, possibleMoves: List<Move>): Boolean {
        return possibleMoves.contains(move)
    }

    fun gameIsRunning(): Boolean {
        return status == GameStatus.STARTED
    }

    fun makeMove(piecePosition: String, destinationPosition: String) {
        val pieceSlot = board.getSlotByPosition(piecePosition)
        val piece = pieceSlot.piece
        if (piece == null || piece.color != actualPlayerColor) return

        val destinationSlot = board.getSlotByPosition(destinationPosition)
        val isCapture = destinationSlot.piece != null
        val move = Move(pieceSlot, destinationSlot, isCapture)
        val possibleMoves = board.getLegalMoves(pieceSlot)
        if (checkPossibleMove(move, possibleMoves)) {
            board.movePiece(move)
            if (isKingInCheck())  println("O rei esta em xeque")
            board.showBoard()
            changePlayerColor()
            checkStatus()
        }
    }

    init {
        board.showBoard()
    }
}