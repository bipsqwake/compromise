export type Decision = "OK" | "NOT_OK"

export type DecisionMessage = {
    cardId: string,
    decision: Decision
}