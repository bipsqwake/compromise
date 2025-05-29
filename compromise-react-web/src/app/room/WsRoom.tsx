import { useContext, useEffect, useRef, useState } from "react";
import { useStompClient } from "react-stomp-hooks";
import type { Card } from "../../types/ws/Card";
import type { PlayerStatusMessage } from "../../types/ws/PLayersStatusMessage";
import type { StatusMessage } from "../../types/ws/StatusMessage";
import { RoomInfoContext } from "../routes/Room";
import RoomCard from "./RoomCard";
import RoomLobby from "./RoomLobby";

export default function WsRoom() {
    const { roomInfo, setRoomInfo } = useContext(RoomInfoContext);
    const stompClient = useStompClient();

    const [playersList, setPlayersList] = useState([""])
    const [currentCard, setCurrentCard] = useState<Card | undefined>(undefined)
    const cardsList = useRef<Card[]>([])
    const [waiting, setWaiting] = useState(false);
    const waitingRef = useRef(false)

    waitingRef.current = waiting

    useEffect(() => {
        if (stompClient && stompClient.connected) {
            stompClient.subscribe(`/topic/room/${roomInfo.roomId}/players`, (message) => receivePlayers(JSON.parse(message.body) as PlayerStatusMessage))
            stompClient.subscribe(`/topic/room/${roomInfo.roomId}/status`, (message) => receiveStatus(JSON.parse(message.body) as StatusMessage))
            stompClient.subscribe(`/user/queue/cards`, (message) => receiveCard(JSON.parse(message.body) as Card))
            stompClient.publish({
                destination: `/server/room/${roomInfo.roomId}/hello`,
                body: JSON.stringify({ name: roomInfo.username }),
            });
            return () => {
            }
        }
    }, [stompClient]);

    function receivePlayers(message: PlayerStatusMessage) {
        if (roomInfo.state == "LOBBY") {
            setPlayersList(message.playerNames);
        }
    }

    function receiveStatus(message: StatusMessage) {
        if (message.status == "STARTED") {
            setRoomInfo({ ...roomInfo, state: "PROCESS" });
        }
    }

    function receiveCard(message: Card) {
        console.log("Pushed card")
        cardsList.current.push(message);
        console.log("AFter Pushed card " + waiting)
        if (waitingRef.current) {
            console.log("Reset waiting")
            setWaiting(false);
        }
    }


    if (roomInfo.state == "LOBBY") {
        return <RoomLobby playersList={playersList} />
    } else if (roomInfo.state == "PROCESS") {
        if (currentCard != undefined) {
            return <RoomCard card={currentCard} />
        } else {
            console.log("w " + waiting)
            console.log(!waiting && cardsList.current.length > 0)
            if (!waiting && cardsList.current.length > 0) {
                var nextCard = cardsList.current.pop();
                setCurrentCard(nextCard);
            } else if (!waiting){
                setWaiting(true)
            }
        }
    }
}