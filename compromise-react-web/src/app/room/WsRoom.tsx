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
import RoomImage from "../components/RoomImage";
import RoomNoSelectedCard from "./RoomNoSelectedCard";

export default function WsRoom() {
    const { roomInfo, setRoomInfo } = useContext(RoomInfoContext);
    const stompClient = useStompClient();

    const [playersList, setPlayersList] = useState([""]);
    const [currentCard, setCurrentCard] = useState<Card[] | undefined>(undefined);
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
            setRoomInfo({ ...roomInfo, state: "FINISH" });
        } else if (stateRef.current == "PROCESS" && message.status == "FINISHED_NO_CARD") {
            console.log("Finish received");
            selectedCard.current = message.selectedCard;
            setRoomInfo({ ...roomInfo, state: "FINISH_NO_CARD" });
        }
    }

    function receiveCard(message: Card) {
        cardsList.current.push(message);
        const img = new Image();
        img.src = message.img;
        setCurrentCard(cardsList.current.slice().reverse());
        // if (waitingRef.current) {
        //     setWaiting(false);
        // }
    }

    function makeDecision(cardId: string, decision: Decision) {
        if (stompClient && stompClient.connected) {
            stompClient.publish({
                destination: `/server/room/${roomInfo.roomId}/decision`,
                body: JSON.stringify({ cardId: cardId, decision: decision }),
            });
        }
        cardsList.current = cardsList.current.filter(c => c.id != cardId)
        setCurrentCard(cardsList.current.slice().reverse());
    }


    if (roomInfo.state == "LOBBY") {
        return <RoomLobby playersList={playersList} />
    } else if (roomInfo.state == "PROCESS") {
        console.log("Checking card " + currentCard)
        if (currentCard != undefined && currentCard.length > 0) {
            return (
                <div className="process-container">
                    <section className="card-container">
                        {currentCard.map(c => <RoomCard key={c.id} card={c} decisionFn={makeDecision} />)}
                        {/* <RoomCard key={currentCard.id} card={currentCard} decisionFn={makeDecision} /> */}
                    </section>
                    <div className="like-buttons">
                        <button className="like-btn not-ok" onClick={() => makeDecision(currentCard[currentCard.length - 1].id, "NOT_OK")}><i className="fa-solid fa-xmark"></i> NOT OK</button>
                        <button className="like-btn ok" onClick={() => makeDecision(currentCard[currentCard.length - 1].id, "OK")}><i className="fa-solid fa-check"></i> OK</button>
                    </div>
                </div>

            );
        } else {
            return (
                <div className="process-container">
                    <RoomImage url={"https://cool-pictures.su/wp-content/uploads/2019/04/15/213634885.gif"} />
                    <section className="card-container">
                        Ой... Карточки закончились. Ждём остальных участников
                    </section>   
                </div>
            );
        }
    } else if (roomInfo.state == "FINISH" && selectedCard.current) {
        console.log("Finished " + selectedCard.current)
        return <RoomSelectedCard card={selectedCard.current} />
    } else if (roomInfo.state == "FINISH_NO_CARD") {
        return <RoomNoSelectedCard />
    }
}