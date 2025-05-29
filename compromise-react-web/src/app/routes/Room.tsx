import { createContext, useContext, useState } from "react";
import { useParams } from "react-router";
import CompromiseRoomApiContext from "../../api/CompromiseRoomApiContext";
import type { RoomInfo } from "../../types/RoomInfo";
import RoomStateRouter from "../room/RoomStateRouter";

export const RoomInfoContext = createContext<{ roomInfo: RoomInfo; setRoomInfo: React.Dispatch<React.SetStateAction<RoomInfo>> }> ({} as { roomInfo: RoomInfo; setRoomInfo: React.Dispatch<React.SetStateAction<RoomInfo>> })

export default function Room() {
    const params = useParams();
    const roomId = params.roomId;

    const [roomInfo, setRoomInfo] = useState<RoomInfo>({ username: undefined, roomName: undefined, roomId: roomId == undefined ? "" : roomId, state: "INITIALIZED"})

    const api = useContext(CompromiseRoomApiContext);
    const { isError, isPending, data, error } = api.useGetRoomName(roomId == undefined ? "" : roomId);

    if (isError) {
        console.log(error)
        return <div>Err</div>;
    }

    if (isPending) {
        return <div>Spinner</div>;
    }

    const roomName = data.data.name;

    if (roomInfo.roomName == undefined) {
        setRoomInfo({...roomInfo, roomName: roomName})
    }

    return (
        <RoomInfoContext.Provider value={{roomInfo, setRoomInfo}}>
            <RoomStateRouter />
        </RoomInfoContext.Provider>
    );
    
}