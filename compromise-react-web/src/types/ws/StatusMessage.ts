import type { Card } from "./Card";

export type Status = "STARTED" | "FINISHED" | "FINISHED_NO_CARD";

export type StatusMessage = {
    status: Status;
}

export type FinishMessage = StatusMessage & {
    selectedCard: Card | undefined;
}

