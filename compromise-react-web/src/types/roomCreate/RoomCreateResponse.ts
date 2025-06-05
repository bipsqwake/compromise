export type RoomResponseStatus = "PREPARE" | "IN_PROGRESS" | "FINISHED";

export type RoomResponse = {
    id: string,
    name: string,
    state: RoomResponseStatus
}