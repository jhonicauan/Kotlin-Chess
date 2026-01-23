export interface Piece {
    color: PieceColor,
    pieceType: PieceType
}

export interface Slot {
    position: String,
    piece: Piece | null
}

export interface GameStatus {
    status: Status,
    actualColor: PieceColor,
    isCheck: Boolean
}

export interface Game {
    board: Slot[],
    moveLog: MoveLog[],
    status: GameStatus
}

export interface MoveLog {
    round: number,
    notation: String
}

export type Status = "RUNNING" | "BLACK_WINS" | "WHITE_WINS" | "STALEMATE"
export type PieceColor = "WHITE" | "BLACK"
export type PieceType = "KING" | "QUEEN" | "BISHOP" | "KNIGHT" | "ROOK" | "PAWN"
