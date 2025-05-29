export type PlayerStatusMessage = {
    action: PlayersAction,
    name: string,
    playerNames: string[]
}

export type PlayersAction = "CONNECTED" | "DISCONNECTED"