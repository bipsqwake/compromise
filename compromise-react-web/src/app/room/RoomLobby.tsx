import { useStompClient } from "react-stomp-hooks";
import { RoomInfoContext } from "../routes/Room";
import { useContext } from "react";

export default function RoomLobby({ playersList }: { playersList: string[] }) {
    const url = window.location.href;
    const stompClient = useStompClient();
    const { roomInfo, setRoomInfo } = useContext(RoomInfoContext);

    function startRoom() {
        if (stompClient && stompClient.connected) {
            stompClient.publish({
                destination: `/server/room/${roomInfo.roomId}/start`,
                body: "{}"
            })
        } else {
            console.log("Failed to start");
        }
    }

    function PlayersList({ list }: { list: string[] }) {
        return (
            <table className="users-table">
                <thead>
                    <tr><th>Участники</th></tr>
                </thead>
                <tbody>
                    {list.map(element => <tr key={element}><td>{element}</td></tr>)}
                </tbody>
            </table>
        );
    }

    return (
        <section>
            <div className="input-copy-group">
                <input type="text" value={url} id="room-link" readOnly />
                <button onClick={() => { navigator.clipboard.writeText(url) }}>📋</button>
            </div>
            <img src={`https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${url}`} alt="QR-код комнаты" className="qr-code" />
            <PlayersList list={playersList}/>
            <div className="start-button-container">
                <a href="#" onClick={startRoom} role="button" className="primary start-button">Начать</a>
            </div>
        </section>
    );
}