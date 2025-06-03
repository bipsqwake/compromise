import axios, { type AxiosResponse } from "axios";
import { useForm } from "react-hook-form";
import type { RoomCreateRequest } from "../../types/roomCreate/RoomCreateRequest";
import type { RoomResponse } from "../../types/roomCreate/RoomCreateResponse";

type Inputs = {
    source: "BGG" | "TESERA",
    username: string,
    playersNum: number
}

export default function BgPrepare() {

    const {
        register,
        handleSubmit
    } = useForm<Inputs>()
    
    const onSubmit = (data: Inputs) => {

        axios.post<RoomCreateRequest, AxiosResponse<RoomResponse>>(import.meta.env.VITE_API_URL + "/rooms/create/boardgames", {...data, name: "Коллекция игрока " + data.username})
            .then(function (response) {
                const roomId = response.data.id
                window.location.replace(`/rooms/${roomId}`)
            });
    }

    return (
        <section className="connect-container" onSubmit={handleSubmit(onSubmit)}>
            <form>
                <label htmlFor="source">Источник:</label>
                <select id="source" {...register("source", {required: true})}>
                    <option value="BGG">BGG</option>
                    <option value="TESERA">Tesera</option>
                </select>

                <label htmlFor="username">Имя пользователя:</label>
                <input {...register("username", {required: true})} />

                <label htmlFor="players">Количество игроков:</label>
                <input {...register("playersNum", {required: true})} />

                <button type="submit">Поиск</button>
            </form>
        </section>
    );
}