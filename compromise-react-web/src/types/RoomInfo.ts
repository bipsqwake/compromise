import type { RoomStatus } from "./RoomStatus"

export type RoomInfo = {
    username: string | undefined,
    roomName: string | undefined,
    roomId: string | undefined,
    state: RoomStatus
}