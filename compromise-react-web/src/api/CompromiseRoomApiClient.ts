import type { AxiosInstance, AxiosResponse } from "axios";
import axios from "axios";
import type { ErrorResponse } from "./ErrorResponse";
import { useMutation, useQuery } from "@tanstack/react-query";
import type { RoomCreateRequest } from "../types/roomCreate/RoomCreateRequest";
import type { RoomResponse } from "../types/roomCreate/RoomCreateResponse";

export class CompromiseRoomApiClient {
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
            mutationFn: (name: string) => {
                return this.axios.post<RoomCreateRequest, AxiosResponse<RoomResponse>>("/rooms/create", {name: name})
            }
        })
    }

    useGetRoomName(id: string) {
        return useQuery({
            queryKey: ["roomId", id],
            queryFn: () => {
                return this.axios.get<RoomResponse>("/rooms/" + id);
            }
        })
    }
}