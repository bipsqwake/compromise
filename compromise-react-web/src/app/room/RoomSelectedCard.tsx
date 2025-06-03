import type { Card } from "../../types/ws/Card";
import HomeButton from "../components/HomeButton";
import Image from "../components/RoomImage";

export default function RoomSelectedCard({ card }: { card: Card }) {
    return (
        <section className="process-container">
            <h1>Это мэтч!</h1>
            <div className="room-content">
                <Image url={card.img} />
                <div className="room-title">{card.name}</div>
                <div className="room-desc">{card.description}</div>
            </div>
            <HomeButton />
        </section>
    );
}