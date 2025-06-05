import { createContext, useContext, useState } from "react";
import { useParams } from "react-router";
import CompromiseRoomApiContext from "../../api/CompromiseRoomApiContext";
import type { RoomInfo } from "../../types/RoomInfo";
import RoomStateRouter from "../room/RoomStateRouter";
import Stub from "../room/Stub";

export const RoomInfoContext = createContext<{ roomInfo: RoomInfo; setRoomInfo: React.Dispatch<React.SetStateAction<RoomInfo>> }> ({} as { roomInfo: RoomInfo; setRoomInfo: React.Dispatch<React.SetStateAction<RoomInfo>> })

export default function Room() {
    const [connected, setConnected] = useState(false)

    const params = useParams();
    const roomId = params.roomId;

    const [roomInfo, setRoomInfo] = useState<RoomInfo>({ username: undefined, roomName: undefined, roomId: roomId == undefined ? "" : roomId, state: "INITIALIZED"})

    const api = useContext(CompromiseRoomApiContext);
    const { isError, isPending, data, error } = api.useGetRoomName(roomId == undefined ? "" : roomId);

    if (isError) {
        console.log(error)
        return <Stub img={"/img/notfound.jpg"} header={"Тут ничего нет"} title={"Никто ещё не создал это голосование"} desc={"Создайте его сами!"} />
    }

    if (isPending) {
        return <div>Spinner</div>;
    }

    const roomName = data.data.name;
    const roomState = data.data.state;

    if (roomInfo.roomName == undefined) {
        setRoomInfo({...roomInfo, roomName: roomName})
    }

    if (roomState != "PREPARE" && !connected) {
        return <Stub img={"/img/guard.jpg"} header={"Вы опоздали"} title={"Это голосование уже началось"} desc={"Возможно без вас они примут лучшее решение"} />
    } else if (!connected) {
        setConnected(true)
    }

    return (
        <RoomInfoContext.Provider value={{roomInfo, setRoomInfo}}>
            <RoomStateRouter />
        </RoomInfoContext.Provider>
    );
    
}