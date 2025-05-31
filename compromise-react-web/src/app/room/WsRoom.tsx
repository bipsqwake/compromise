import { useContext, useEffect, useRef, useState } from "react";
import { useStompClient } from "react-stomp-hooks";
import type { Card } from "../../types/ws/Card";
import type { FinishMessage } from "../../types/ws/StatusMessage";
import { RoomInfoContext } from "../routes/Room";
import RoomCard from "./RoomCard";
import RoomLobby from "./RoomLobby";
import type { Decision } from "../../types/ws/DecisionMessage";
import RoomSelectedCard from "./RoomSelectedCard";
import type { PlayerStatusMessage } from "../../types/ws/PlayersStatusMessage";

export default function WsRoom() {
    const { roomInfo, setRoomInfo } = useContext(RoomInfoContext);
    const stompClient = useStompClient();

    const [playersList, setPlayersList] = useState([""]);
    const [currentCard, setCurrentCard] = useState<Card | undefined>(undefined);
    const selectedCard = useRef<Card | undefined>(undefined);
    const cardsList = useRef<Card[]>([]);
    const [waiting, setWaiting] = useState(false);
    const waitingRef = useRef(false);
    const stateRef = useRef("");

    waitingRef.current = waiting;
    stateRef.current = roomInfo.state;


    useEffect(() => {
        if (stompClient && stompClient.connected) {
            stompClient.subscribe(`/topic/room/${roomInfo.roomId}/players`, (message) => receivePlayers(JSON.parse(message.body) as PlayerStatusMessage))
            stompClient.subscribe(`/topic/room/${roomInfo.roomId}/status`, (message) => receiveStatus(JSON.parse(message.body) as FinishMessage))
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
        if (stateRef.current == "LOBBY") {
            setPlayersList(message.playerNames);
        }
    }

    function receiveStatus(message: FinishMessage) {
        console.log(message as FinishMessage)
        if (stateRef.current == "LOBBY" && message.status == "STARTED") {
            setRoomInfo({ ...roomInfo, state: "PROCESS" });
        } else if (stateRef.current == "PROCESS" && message.status == "FINISHED") {
            console.log("Finish received");
            selectedCard.current = message.selectedCard;
            setRoomInfo({...roomInfo, state: "FINISH"});
        }
    }

    function receiveCard(message: Card) {
        cardsList.current.push(message);
        if (waitingRef.current) {
            console.log("Reset waiting")
            setWaiting(false);
        }
    }

    function makeDecision(cardId: string, decision: Decision) {
        if (stompClient && stompClient.connected) {
            stompClient.publish({
                destination: `/server/room/${roomInfo.roomId}/decision`,
                body: JSON.stringify({cardId: cardId, decision: decision}),
            });
        }
        setCurrentCard(undefined);
    }


    if (roomInfo.state == "LOBBY") {
        return <RoomLobby playersList={playersList} />
    } else if (roomInfo.state == "PROCESS") {
        if (currentCard != undefined) {
            return <RoomCard card={currentCard} decisionFn={makeDecision}/>
        } else {
            if (!waiting && cardsList.current.length > 0) {
                var nextCard = cardsList.current.shift();
                setCurrentCard(nextCard);
            } else if (!waiting){
                setWaiting(true)
            }
        }
    } else if (roomInfo.state == "FINISH" && selectedCard.current) {
        console.log("Finished " + selectedCard.current)
        return <RoomSelectedCard card={selectedCard.current} />
    } 
}