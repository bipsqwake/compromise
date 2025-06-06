import { useContext } from "react";
import { StompSessionProvider } from "react-stomp-hooks";
import { RoomInfoContext } from "../routes/Room";
import RoomHello from "./RoomHello";
import WsRoom from "./WsRoom";
import Stub from "./Stub";

export default function RoomStateRouter() {
    const { roomInfo, setRoomInfo } = useContext(RoomInfoContext)

    function toLobby(name: string) {
        setRoomInfo({ ...roomInfo, username: name, state: "LOBBY" });
    }

    if (roomInfo.roomName == undefined) {
        return <div>Error</div>
    }

    if (roomInfo.state == "INITIALIZED") {
        return <RoomHello name={roomInfo.roomName} onInput={toLobby} />
    } else if (roomInfo.state == "CLOSED") {
        return <Stub img={"/img/notfound.jpg"} header={"Тут ничего нет"} title={"Никто ещё не создал это голосование"} desc={"Создайте его сами!"} />
    } else {
        return (
            <StompSessionProvider url={import.meta.env.VITE_WS_URL}>
                <WsRoom />
            </StompSessionProvider>
        );
    }
}