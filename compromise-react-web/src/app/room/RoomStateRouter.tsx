import { useContext } from "react";
import { StompSessionProvider } from "react-stomp-hooks";
import { RoomInfoContext } from "../routes/Room";
import RoomHello from "./RoomHello";
import WsRoom from "./WsRoom";

export default function RoomStateRouter() {
    const {roomInfo, setRoomInfo} = useContext(RoomInfoContext)

    function toLobby(name: string) {
        setRoomInfo({...roomInfo, username: name, state: "LOBBY"});
    }

    if (roomInfo.roomName == undefined) {
        return <div>Error</div>
    }

    if (roomInfo.state == "INITIALIZED") {
        return <RoomHello name={roomInfo.roomName} onInput={toLobby} />
    } else {
        return (
            <StompSessionProvider url={"ws://localhost:8081/compromise"}>
                <WsRoom />
            </StompSessionProvider>
        );
    }
}