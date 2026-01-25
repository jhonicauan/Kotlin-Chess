import { Client, type IFrame } from "@stomp/stompjs"
import SockJS from "sockjs-client/dist/sockjs"
import { useEffect, useRef, useState } from "react"
import "./types/ChessTypes"
import "./board.css"

// peças pretas
import blackKing from "./assets/blackKing.svg"
import blackQueen from "./assets/blackQueen.svg"
import blackRook from "./assets/blackRook.svg"
import blackBishop from "./assets/blackBishop.svg"
import blackKnight from "./assets/blackKnight.svg"
import blackPawn from "./assets/blackPawn.svg"

// peças brancas
import whiteKing from "./assets/whiteKing.svg"
import whiteQueen from "./assets/whiteQueen.svg"
import whiteRook from "./assets/whiteRook.svg"
import whiteBishop from "./assets/whiteBishop.svg"
import whiteKnight from "./assets/whiteKnight.svg"
import whitePawn from "./assets/whitePawn.svg"
import { type MoveLog,type Status, type Game,  type Slot, type GameStatus,type PieceColor, type Round } from "./types/ChessTypes"

function Board() {


    const clientRef = useRef<Client | null>(null)


    const [board, setBoard] = useState<Slot[]>([])
    const [selectedBoard, setSelectedBoard] = useState<Slot[]>([])
    const [moveLog, setMoveLog] = useState<MoveLog[]>([])
    const [status, setStatus] = useState<Status>("RUNNING")
    const [isCheck,setIsCheck] = useState<Boolean>(false)
    const [currentColor,setCurrentColor] = useState<PieceColor>("WHITE")
    const [room,setRoom] = useState("Sala1")
    const columns = ["a", "b", "c", "d", "e", "f", "g", "h"]
    const rows = [8,7,6,5,4,3,2,1]
    const [legalMoves,setLegalMoves] = useState<Slot[]>([])
    const [selectedPosition,setSelectedPosition] = useState("")

    const pieceImages = {
        BLACK: {
            KING: blackKing,
            QUEEN: blackQueen,
            ROOK: blackRook,
            BISHOP: blackBishop,
            KNIGHT: blackKnight,
            PAWN: blackPawn
        },
        WHITE: {
            KING: whiteKing,
            QUEEN: whiteQueen,
            ROOK: whiteRook,
            BISHOP: whiteBishop,
            KNIGHT: whiteKnight,
            PAWN: whitePawn
        }
    }


    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
            reconnectDelay: 5000
        })

        client.onConnect = (frame: IFrame) => {
            console.log("STOMP conectado")
            client.subscribe("/topic/game/"+room, response => {
                const game: Game = JSON.parse(response.body)
                const gameStatus: GameStatus = game.status
                setBoard(game.board)
                setSelectedBoard(game.board)
                setStatus(gameStatus.status)
                setCurrentColor(gameStatus.actualColor)
                setIsCheck(gameStatus.isCheck)
                setMoveLog(game.moveLog)
            })

            client.subscribe("/user/queue/check/"+room, response => {
                setLegalMoves(JSON.parse(response.body))
            })

            client.publish({destination:"/app/game/create",body:room})
            client.publish({
                destination: "/app/game/joined/"+room,
                body: ""
            })
        }

        client.activate()
        clientRef.current = client

        return () => {
            client.deactivate()
        }
    }, [])

    function setPieceImage(position: string) {
        const slot = selectedBoard.find(slot => slot.position === position)
        if (!slot || !slot.piece) return null

        const piece = slot.piece
        return (
            <img
            src={pieceImages[piece.color][piece.pieceType]}
            alt={`${piece.color} ${piece.pieceType}`}
            />
        )
        }

    function setSlotColor(row: number, column: number) {
        if(selectedBoard.length == 0) return
        const position = columns[column]+rows[row]
        const slot = selectedBoard.find((slot)=>slot.position === position)
        if (slot == null) throw new Error("Slot não existe")
        if (slot.piece == null)  {
            if (isSlotALegalMove(position)) return "square blue"
            if(moveLog.length != 0) {
            const log = moveLog.find((log) => log.roundBoard == selectedBoard) ?? moveLog[moveLog.length - 1]
            const piecePosition = log.piecePosition
            const destinyPosition = log.destinyPosition
            if (piecePosition == position || destinyPosition == position) return "square yellow"
        }
            return (row + column) % 2 == 0 ? "square white" : "square black"
        } 
        if (slot.piece.color == currentColor && slot.piece.pieceType == "KING" && isCheck) return "square red"
        if (isSlotALegalMove(position) && slot.piece.color == currentColor) return "square blue"
        else if (isSlotALegalMove(position)) return "square green"
        if(moveLog.length != 0) {
            const log = moveLog.find((log) => log.roundBoard == selectedBoard) ?? moveLog[moveLog.length - 1]
            const piecePosition = log.piecePosition
            const destinyPosition = log.destinyPosition
            if (piecePosition == position || destinyPosition == position) return "square yellow"
        }
        return (row + column) % 2 == 0 ? "square white" : "square black"
    }
    
    function showStatus() {
        if (status == "RUNNING") {
            return <h1>Jogo em andamento</h1>
        } else if(status == "BLACK_WINS") {
            return <h1>Vitoria das pretas</h1>
        } else if(status == "WHITE_WINS") {
            return <h1>Vitoria das brancas</h1>
        } 
        return <h1>Empate</h1>
    }

    function isSlotALegalMove(position: String) {
       return legalMoves.some(slot => slot.position === position)
    }

    function slotIsCurrentColor(position: String) {
        const slot = board.find(slot => slot.position == position)
        return slot?.piece == null || slot?.piece?.color == currentColor 
    }

    function selectSlot(position: string) {
        if (selectedBoard === board) {
        const client = clientRef.current
        if (!client || !client.connected) {
            console.warn("STOMP não conectado")
            return
        }
        if(!isSlotALegalMove(position)){
        if(!slotIsCurrentColor(position)) return
        setSelectedPosition(position)
        client.publish({
            destination: "/app/game/checkMoves/"+room,
            body: position
        })
    }else{
        if(!isMovePromotion()) {
        client.publish({
            destination: "/app/game/makeMove/"+room,
            body: JSON.stringify({
                "position": selectedPosition,
                "destiny": position,
                "promotion": null
            })
        })
    } else {
        const promotion = prompt("Digite a peça desejada")
        client.publish({
            destination: "/app/game/makeMove/"+room,
            body: JSON.stringify({
                "position": selectedPosition,
                "destiny": position,
                "promotion": promotion
            })
        })
    }
        setLegalMoves([])
        setSelectedPosition("")
    }
}
    }

    function groupByRound() {
        const rounds: Round[] = []
        moveLog.forEach((move,index) => {
            if (index % 2 == 0) {
                rounds.push({round: move.round,white: move, black: null})
            }else {
                const round = rounds[rounds.length - 1]
                round.black = move
            }
        })
        return rounds
    }

    function isMovePromotion() {
        const promotionRow = currentColor == "WHITE" ? "7" : "2"
        const selectedSlot = board.find((slot)=> slot.position === selectedPosition)
        if(selectedSlot == null) throw new Error("Slot não existe")
        return promotionRow == selectedSlot.position[1] && selectedSlot.piece?.pieceType == "PAWN"
    }

    function selectBoard(selectBoard: Slot[]) {
        if(selectBoard == moveLog[moveLog.length -1].roundBoard) {
            setSelectedBoard(board)
        }else{
            setSelectedBoard(selectBoard)
        }
    }

    return (
        <>
       <table id="board">
        <tbody>
        {rows.map((row,indexRow) =>(
            <tr>
                <th>{row}</th>
                {columns.map((column, indexColumn)=>{
                    const position = column+row
                    return <th className={setSlotColor(indexRow,indexColumn)} onClick={() => selectSlot(position)}>{setPieceImage(position)}</th>
                })}
            </tr>
        ))}
        <tr>
            <th></th>
           {columns.map((column) =>(
            <th>{column}</th>
           ))}
        </tr>
        </tbody>
       </table>
       {showStatus()}
       <table className="log">
        <tr>
            <th>Rodada</th>
            <th>Movimento Brancas</th>
            <th>Movimento Pretas</th>
        </tr>
        {groupByRound().map((round)=>(
            <tr><th>{round.round}</th>
            <th onClick={()=>selectBoard(round.white.roundBoard)}>{round.white.notation}</th>
            <th onClick={()=>round.black && selectBoard(round.black.roundBoard)}>{round.black == null ? "" : round.black.notation}</th></tr>
        ))}
        </table>
        </>
    )
}

export default Board
