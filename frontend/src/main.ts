import { Client, type IFrame } from "@stomp/stompjs"
import SockJS from "sockjs-client/dist/sockjs"


const client: Client = new Client({
  webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
  reconnectDelay: 5000,
})

client.onConnect = (frame: IFrame) => {
  console.log("Conectado")
}

client.activate()
