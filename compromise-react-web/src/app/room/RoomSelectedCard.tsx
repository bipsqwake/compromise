import type { Card } from "../../types/ws/Card";
import Image from "../components/Image";

export default function RoomSelectedCard({ card }: { card: Card }) {
    return (
        <section className="process-section">
            <h1>Это мэтч!</h1>
            <div className="room-content">
                <Image url={card.img} />
                <div className="room-title">{card.name}</div>
                <div className="room-desc">{card.description}</div>
            </div>
        </section>
    );
}