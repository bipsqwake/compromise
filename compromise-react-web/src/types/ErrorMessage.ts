export type ErrorMessage = {
    error: "extraction_failed" | "invalid_input" | "small_collection" | undefined,
    errorDescription: string
}