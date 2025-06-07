
import axios, { type AxiosResponse } from "axios";
import type { RoomCreateRequest } from "../../types/roomCreate/RoomCreateRequest";
import type { RoomResponse } from "../../types/roomCreate/RoomCreateResponse";

export default function QuickRooms() {

    function createFoodRoom() {
        axios.post<RoomCreateRequest, AxiosResponse<RoomResponse>>(import.meta.env.VITE_API_URL + "/rooms/create/food", { name: "Еда" })
            .then(function (response) {
                const roomId = response.data.id
                window.location.replace(`/rooms/${roomId}`)
            });
    }

    function createBggRoom() {
        window.location.replace(`/prepare/boardgame`)
    }


    return (
        <div>
            <header>
                <h1>Synkro</h1>
                <hr />
            </header>
            <p className="slogan">Всем нравится? Тогда берём!</p>
            <div className="button-group">
                {/* <a href="#" role="button" className="contrast">Войти</a> */}
                <a href="#" onClick={createBggRoom} role="button">Настольные игры (BGG и Tesera)</a>
                <a href="#" onClick={createFoodRoom} role="button">Еда</a>
            </div>
        </div>
    );
}