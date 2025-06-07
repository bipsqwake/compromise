import type { ErrorMessage } from "../types/ErrorMessage";

export function mapError(error: ErrorMessage) : string {
    switch (error.error) {
        case "extraction_failed":
            return "Ошибка при получении коллекции. Попробуйте позже";
        case "small_collection":
            return "Коллекция слишком маленькая, чтобы запустить голосование";
        case "invalid_input":
        default:
            return "Ой, что-то пошло не так";
        
    }
}