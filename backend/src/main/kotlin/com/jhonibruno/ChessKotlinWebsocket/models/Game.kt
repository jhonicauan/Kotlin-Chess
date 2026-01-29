package com.jhonibruno.ChessKotlinWebsocket.models

import com.jhonibruno.ChessKotlinWebsocket.models.board.Board
import com.jhonibruno.ChessKotlinWebsocket.models.board.Slot
import com.jhonibruno.ChessKotlinWebsocket.models.client.ClientBoardDTO
import com.jhonibruno.ChessKotlinWebsocket.models.client.GameDTO
import com.jhonibruno.ChessKotlinWebsocket.models.client.GameStatusDTO
import com.jhonibruno.ChessKotlinWebsocket.models.enums.GameStatus
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceColor
import com.jhonibruno.ChessKotlinWebsocket.models.enums.PieceType
import com.jhonibruno.ChessKotlinWebsocket.models.history.MoveLogRegistry
import com.jhonibruno.ChessKotlinWebsocket.models.notation.MoveNotation
import com.jhonibruno.ChessKotlinWebsocket.models.pieces.Piece

class Game(private val board: Board) {

    private var currentPlayerColor: PieceColor = PieceColor.WHITE
    private var status = GameStatus.RUNNING
    private val moveLog = mutableListOf<MoveLogRegistry>()
    private var isCheck = false

    private fun changePlayerColor() {
        currentPlayerColor = if (currentPlayerColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
    }

    // TODO: deixar mais claro que atribui uma propriedade
    private fun verifyCheck() {
        isCheck = board.isKingInCheck(currentPlayerColor)
    }

    private fun isCheckmate(): Boolean {
        return isCheck && !board.existsSafeMoves(currentPlayerColor)
    }

    private fun isStalemate(): Boolean {
        return !isCheck && !board.existsSafeMoves(currentPlayerColor)
    }

    fun updateStatus() {
        if (isCheckmate()) {
            status = when(currentPlayerColor) {
                PieceColor.WHITE -> GameStatus.BLACK_WINS
                PieceColor.BLACK -> GameStatus.WHITE_WINS
            }
        }
        else if (isStalemate()) status = GameStatus.STALEMATE
    }

    private fun existsPossibleMove(move: Move, possibleMoves: List<Move>): Boolean {
        return possibleMoves.contains(move)
    }

    private fun getBoard(): List<ClientBoardDTO> {
        return board.getSlots().flatten().map { it.toDto() }
    }

    fun getGameDTO(): GameDTO {
        return GameDTO(getBoard(),moveLog.map { it.toDto() }, GameStatusDTO(isCheck, status, currentPlayerColor))
    }

    fun viewPossibleMoves(position: String): List<ClientBoardDTO> {
        val slot = board.getSlotByPosition(position)
        val legalMoves = board.getLegalMoves(slot)
        val legalSlots = mutableListOf<ClientBoardDTO>()
        for (move in legalMoves) {
            legalSlots.add(move.destinationSlot.toDto())
        }
        return legalSlots
    }

    private fun isRunning(): Boolean {
        return status == GameStatus.RUNNING
    }

    private fun getRound(): Int {
        if(moveLog.isEmpty()) return 1
        return if (moveLog.size % 2  != 0) moveLog.last().round
        else moveLog.last().round + 1
    }

    private fun isMoveKingSideCastling(move: Move): Boolean {
        val slots = board.getSlots()
        val colorRow = if (currentPlayerColor == PieceColor.WHITE) 0 else 7
        return move.pieceSlot.piece?.type == PieceType.KING && move.pieceSlot == slots[colorRow][4] && move.destinationSlot == slots[colorRow][6]
    }

    private fun isMoveQueenSideCastling(move: Move): Boolean {
        val slots = board.getSlots()
        val colorRow = if (currentPlayerColor == PieceColor.WHITE) 0 else 7
        return move.pieceSlot.piece?.type == PieceType.KING && move.pieceSlot == slots[colorRow][4] && move.destinationSlot == slots[colorRow][2]
    }

    fun makeMove(moveDTO: ClientMoveDTO) {
        if (!isRunning()) throw IllegalStateException("O jogo já terminou")
        val piecePosition = moveDTO.position
        val pieceSlot = board.getSlotByPosition(piecePosition)
        val piece = pieceSlot.piece

        if (piece == null || piece.color != currentPlayerColor) throw IllegalArgumentException("Só é possível mover peças de sua cor")

        val destinationPosition = moveDTO.destiny
        val destinationSlot = board.getSlotByPosition(destinationPosition)

        val isCapture = destinationSlot.piece != null
        val move = Move(pieceSlot, destinationSlot, isCapture)
        val possibleMoves = board.getLegalMoves(pieceSlot)

        if (!existsPossibleMove(move, possibleMoves)) return

        val moveSlotsOriginalState = move.clone()

        var alternativePieces: List<Slot> = listOf()

        val isCastlingKing = isMoveKingSideCastling(move)
        val isCastlingQueen = isMoveQueenSideCastling(move)

        if (isCastlingKing) {
            board.kingSideCastling(currentPlayerColor)
        } else if (isCastlingQueen) {
            board.queenSideCastling(currentPlayerColor)
        } else {
            alternativePieces = board.getAlternativePiece(move) // TODO: MELHORAR NOME DO MÉTODO - peças alternativas = outras peças do mesmo tipo que podem competir pela notação

            checkValidPromotionMove(destinationSlot, piece, moveDTO)
            board.movePiece(move)
        }

        val boardCurrentState = board.getCurrentState().flatten()
        if (board.canPromote(destinationSlot, piece) && moveDTO.promotion != null) board.promotePawn(destinationSlot,moveDTO.promotion)
        changePlayerColor()
        verifyCheck()

        val moveNotation = MoveNotation(moveSlotsOriginalState, alternativePieces, isCheck, isCheckmate(), isCastlingKing, isCastlingQueen, moveDTO.promotion)
        moveLog.add(MoveLogRegistry(getRound(), moveNotation, boardCurrentState))
        updateStatus()
    }

    private fun checkValidPromotionMove(destinationSlot: Slot, piece: Piece, moveDTO: ClientMoveDTO) {
        if (!board.canPromote(destinationSlot, piece)) {
            if (moveDTO.promotion != null) throw IllegalArgumentException("Não é possível promover")
        } else {
            if (moveDTO.promotion == null) throw IllegalArgumentException("Não se pode mover um peão para ultima casa sem especificar a promoção")
        }
    }

    init {
        board.showBoard()
    }
}