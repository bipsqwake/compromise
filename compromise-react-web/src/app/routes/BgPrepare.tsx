import axios, { type AxiosResponse } from "axios";
import { type CSSProperties } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { BeatLoader } from "react-spinners";
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
        handleSubmit,
        getValues,
        formState: { isSubmitting, isDirty, isValid, errors }
    } = useForm<Inputs>()


    const onSubmit = (data: Inputs) => {
        return axios.post<RoomCreateRequest, AxiosResponse<RoomResponse>>
            (import.meta.env.VITE_API_URL + "/rooms/create/boardgames",
                { ...data, name: "Коллекция игрока " + data.username })
            .then(function (response) {
                const roomId = response.data.id
                window.location.replace(`/rooms/${roomId}`)
            }).catch(function (error) {
                console.log(error);
                toast("Ой, что-то пошло не так");
            });
    }

    const override: CSSProperties = {
        textAlign: "center"
    };

    console.log(isSubmitting)

    return (
        <section className="connect-container" onSubmit={handleSubmit(onSubmit)}>
            <form>
                <label htmlFor="source">Источник:</label>
                <select id="source" {...register("source", { required: true })}>
                    <option value="BGG">BGG</option>
                    <option value="TESERA">Tesera</option>
                </select>

                <label htmlFor="username">Имя пользователя:</label>
                <input aria-invalid={errors.username ? "true" : undefined} {...register("username", { required: true })} />
                <label htmlFor="players">Количество игроков:</label>
                <input aria-invalid={errors.playersNum ? "true" : undefined} {...register("playersNum", { required: true })} />

                <button type="submit" disabled={isSubmitting}>Начать</button>
                {isSubmitting ? <BeatLoader cssOverride={override} /> : ""}
                {(isSubmitting && getValues("source") == "TESERA") ?
                    <p className="loading-notification">Выгрузка коллекции с тесеры может идти дольше обычного</p> :
                    ""}
            </form>
        </section>
    );
}