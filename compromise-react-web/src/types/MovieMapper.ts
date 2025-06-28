export const types = {
    MOVIE: "Фильм",
    TV_SHOW: "Сериал"
}

{/*
    public enum Genre {
        THRILLER,
        HORROR,
        ACTION,
        COMEDY,
        FANTASTIC,
        FANTASY,
        DETECTIVE,
        DRAMA
    }

    public enum Countries {
        RUSSIA,
        USA,
        SPAIN,
        FRANCE
    }

    public enum Service {
        IVI,
        KION,
        KINOPOISK,
        START,
        OKKO,
        PREMIERE
    }
    */}

export const genres = [
    {label: "THRILLER", value: "Триллер"},
    {label: "HORROR", value: "Ужасы"},
    {label: "ACTION", value: "Боевик"},
    {label: "COMEDY", value: "Комедия"},
    {label: "FANTASTIC", value: "Фантастика"},
    {label: "FANTASY", value: "Фэнтези"},
    {label: "DETECTIVE", value: "Детектив"},
    {label: "DRAMA", value: "Драма"},
]

export const countries = [
    {label: "RUSSIA", value: "Россия"},
    {label: "USA", value: "США"},
    {label: "FRANCE", value: "Франция"},
    {label: "SPAIN", value: "Испания"},
]

export const services = [
    {label: "KINOPOISK", value: "Кинопоиск"},
    {label: "IVI", value: "IVI"},
    {label: "OKKO", value: "Okko"},
    {label: "START", value: "Start"},
    {label: "KION", value: "KION"},
    {label: "PREMIERE", value: "Premiere"},
]
