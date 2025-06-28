import axios, { AxiosError, type AxiosResponse } from "axios";
import { type CSSProperties } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { mapError } from "../../api/ErrorMapper";
import "../../filter.css";
import type { ErrorMessage } from "../../types/ErrorMessage";
import { countries, genres, types } from "../../types/MovieMapper";
import type { RoomCreateRequest } from "../../types/roomCreate/RoomCreateRequest";
import type { RoomResponse } from "../../types/roomCreate/RoomCreateResponse";
import { BeatLoader } from "react-spinners";

type Inputs = {
    type: "MOVIE" | "TV_SHOW",
    genresExclude: boolean,
    genres: string[] | null,
    countriesExclude: boolean,
    countries: string[] | null,
    services: string[] | null,
    yearFrom: number,
    yearTo: number,
    minRating: number
}

export default function BgPrepare() {

    const {
        register,
        handleSubmit,
        formState: { isSubmitting }
    } = useForm<Inputs>()


    const onSubmit = (data: Inputs) => {
        if (!data.genres || data.genres.length == 0) {
            data.genres = null;
        }
        if (!data.countries || data.countries.length == 0) {
            data.countries = null;
        }
        if (!data.services || data.services.length == 0) {
            data.services = null;
        }
        return axios.post<RoomCreateRequest, AxiosResponse<RoomResponse>>
            (import.meta.env.VITE_API_URL + "/rooms/create/movies",
                { ...data, name: "Подборка фильмов" })
            .then(function (response) {
                const roomId = response.data.id
                window.location.replace(`/rooms/${roomId}`)
            }).catch(function (error: AxiosError) {
                toast(mapError(error.response?.data as ErrorMessage));
            });
    }

    const typeLabelCss: CSSProperties = {
        fontWeight: 500
    };

    const loaderCss: CSSProperties = {
        textAlign: "center"
    };


    return (
        <section className="connect-container" onSubmit={handleSubmit(onSubmit)}>
            <form>
                <div className="form-row">
                    <label htmlFor="type" style={typeLabelCss}>Тип</label>
                    <select {...register("type", { required: true })}>
                        {Object.entries(types).map(([key, value]) => <option value={key}>{value}</option>)}
                    </select>
                </div>

                <details>
                    <summary>Минимальный рейтинг</summary>
                    <div className="star-rating">
                        {[...Array(10).keys()].reverse().map(i => <><input type="radio" id={"star" + (i + 1)} value={i + 1} {...register("minRating")}/><label htmlFor={"star" + (i + 1)} title={(i + 1) + " звёзд"}>&#9733;</label></>)}
                    </div>
                    <div className="note">Выберите минимальный рейтинг от 1 до 10 звёзд</div>
                </details>

                <details>
                    <summary>Жанры</summary>
                    <div className="exclude-switch">
                        <input type="checkbox" role="switch" {...register("genresExclude")} /> Исключить выбранные жанры
                    </div>
                    <div className="checkbox-group">
                        {genres.map(opt => <label><input type="checkbox" value={opt.label} {...register("genres")} /> {opt.value}</label>)}
                    </div>
                </details>

                <details>
                    <summary>Год выхода</summary>
                    <div className="range-group">
                        <input type="number" placeholder="2000" {...register("yearFrom", { min: 1900 })} />
                        <span>—</span>
                        <input type="number" placeholder="2025" {...register("yearTo", { min: 1900 })} />
                    </div>
                </details>

                <details>
                    <summary>Страны</summary>
                    <div className="exclude-switch">
                        <input type="checkbox" role="switch" {...register("countriesExclude")} /> Исключить выбранные страны
                    </div>
                    <div className="checkbox-group">
                        {countries.map(opt => <label><input type="checkbox" value={opt.label} {...register("countries")} /> {opt.value}</label>)}
                    </div>
                </details>

                {/* <details>
                    <summary>Можно посмотреть на сервисе</summary>
                    <div className="checkbox-group">
                        {services.map(opt => <label><input type="checkbox" value={opt.label} {...register("services")} /> {opt.value}</label>)}
                    </div>
                </details> */}

                <button type="submit" disabled={isSubmitting}>Начать</button>
                {isSubmitting ? <BeatLoader cssOverride={loaderCss} /> : ""}
            </form>
        </section>
    );
}