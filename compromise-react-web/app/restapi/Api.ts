import type { AxiosInstance } from "axios";
import axios from "axios";
import type { ErrorResponse } from "./ErrorResponse";
import { useMutation } from "@tanstack/react-query";
import type { RoomCreateRequest } from "~/types/RoomCreateRequest";
import type { RoomCreateResponse } from "~/types/roomCreatedResponse";

export class CompromiseApiClient {
    axios: AxiosInstance

    constructor(url: string) {
        this.axios = axios.create({
            baseURL: url
        })
        this.axios.interceptors.response.use(function (response) {
            return response
        }, function (error) {
            var errorMessage = error.response.data as ErrorResponse
            console.log(errorMessage.errorDescription)
            return Promise.reject(errorMessage.errorDescription)
        })
    }

    useCreateRoom() {
        return useMutation({
            mutationFn: ({name}: {name: String}) => {
                return this.axios.post<RoomCreateRequest, RoomCreateResponse>("rooms/create", {name: name})
            }
        })
    }
}