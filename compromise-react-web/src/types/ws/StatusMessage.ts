import type { Card } from "./Card";

export type Status = "STARTED" | "FINISHED";

export type StatusMessage = {
    status: Status;
}

export type FinishMessage = StatusMessage & {
    selectedCard: Card | undefined;
}

