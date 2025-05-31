import axios, { type AxiosResponse } from "axios";
import type { RoomCreateRequest } from "../../types/roomCreate/RoomCreateRequest";
import type { RoomResponse } from "../../types/roomCreate/RoomCreateResponse";

export default function Home() {

    function createRoom() {
        axios.post<RoomCreateRequest, AxiosResponse<RoomResponse>>(import.meta.env.VITE_API_URL + "/rooms/create", { name: "Пожрать" })
            .then(function (response) {
                const roomId = response.data.id
                window.location.replace(`/rooms/${roomId}`)
            });

    }

    return (
        <div>
            <header>
                <h1>Compromise</h1>
                <hr />
            </header>
            <p className="slogan">Всем нравится? Тогда берём!</p>
            <div className="button-group">
                <a href="#" role="button" className="contrast">Войти</a>
                <a href="#" onClick={createRoom} role="button">Создать комнату</a>
            </div>
        </div>
    );
}