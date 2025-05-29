import type { Card } from "../../types/ws/Card";

export default function RoomCard({ card }: { card: Card }) {

    return (
        <section className="process-section">
            <img src={card.img} alt="Случайное изображение" className="process-image" />
            <div className="room-content">
                <div className="room-title">{card.name}</div>
                <div className="room-desc">{card.description}</div>
                <div className="button-group">
                    <button className="outline">NOT OK</button>
                    <button className="contrast">OK</button>
                </div>
            </div>
        </section>
    );
}