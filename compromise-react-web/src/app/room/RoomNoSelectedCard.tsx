import HomeButton from "../components/HomeButton";
import Image from "../components/RoomImage";

export default function RoomNoSelectedCard() {
    return (
        <section className="process-container">
            <h1>Это не мэтч(((</h1>
            <div className="room-content">
                <Image url={"https://teamly.ru/storage/upload/news/e8AOFG1YVRpQTqAAzNoWbbnvkFiOXREuRieU6Y1Z.webp"} />
                <div className="room-title">Кажется, вы не смогли прийти к общему решение</div>
                <div className="room-desc">Следующая версия приложения позволит найти вам новых друзей</div>
            </div>
            <HomeButton />
        </section>
    );
}