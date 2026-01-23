package com.jhonibruno.ChessKotlinWebsocket.models

import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import com.jhonibruno.ChessKotlinWebsocket.models.enums.GameStatus
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.notation.MoveNotation

class Game(private val board: Board) {

    var currentPlayerColor: PieceColor = PieceColor.WHITE
    var status = GameStatus.RUNNING

    private fun changePlayerColor() {
        currentPlayerColor = if (currentPlayerColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
    }

    private fun isCheck(): Boolean {
        return board.isKingInCheck(currentPlayerColor) && board.existsSafeMoves(currentPlayerColor)
    }

    private fun isCheckmate(): Boolean {
        return board.isKingInCheck(currentPlayerColor) && !board.existsSafeMoves(currentPlayerColor)
    }

    private fun isStalemate(): Boolean {
        return !board.isKingInCheck(currentPlayerColor) && !board.existsSafeMoves(currentPlayerColor)
    }

    fun updateStatus() {
        if (isCheckmate()) {
            status = when(currentPlayerColor) {
                PieceColor.WHITE -> GameStatus.WHITE_WINS
                PieceColor.BLACK -> GameStatus.BLACK_WINS
            }
        }
        else if (isStalemate()) status = GameStatus.STALEMATE

        if (status == GameStatus.BLACK_WINS) println("Vitoria das pretas")
        if (status == GameStatus.WHITE_WINS) println("Vitoria das brancas")
        if (status == GameStatus.STALEMATE) println("Empate")
    }

    private fun existsPossibleMove(move: Move, possibleMoves: List<Move>): Boolean {
        return possibleMoves.contains(move)
    }

    fun isRunning(): Boolean {
        return status == GameStatus.RUNNING
    }

    fun makeMove(piecePosition: String, destinationPosition: String) {
        if (!isRunning()) throw IllegalStateException("O jogo já terminou")

        val pieceSlot = board.getSlotByPosition(piecePosition)
        val piece = pieceSlot.piece
        if (piece == null || piece.color != currentPlayerColor) throw IllegalArgumentException("Só é possível mover peças de sua cor")

        val destinationSlot = board.getSlotByPosition(destinationPosition)
        val isCapture = destinationSlot.piece != null
        val move = Move(pieceSlot, destinationSlot, isCapture)
        val possibleMoves = board.getLegalMoves(pieceSlot)
        if (existsPossibleMove(move, possibleMoves)) {
            val moveClone = move.clone()
            val alternativePiece = board.getAlternativePiece(move)
            board.movePiece(move)

            changePlayerColor()

            if (isCheck())  println("O rei esta em xeque")

            val moveNotation = MoveNotation(moveClone, alternativePiece, isCheck(), isCheckmate(), false, false, null)
            println(moveNotation.notation)

            board.showBoard()
            updateStatus()
        }
    }

    init {
        board.showBoard()
    }
}