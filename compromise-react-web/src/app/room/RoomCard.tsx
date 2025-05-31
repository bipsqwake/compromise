import type { Card } from "../../types/ws/Card";
import type { Decision } from "../../types/ws/DecisionMessage";
import Image from "../components/Image";

export default function RoomCard({ card, decisionFn }: { card: Card, decisionFn: (arg1: string, arg2: Decision) => void }) {

    return (
        <section className="process-section">
            <div className="room-content">
                <Image url={card.img} />
                <div className="room-title">{card.name}</div>
                <div className="room-desc">{card.description}</div>
                <div className="button-group">
                    <button className="outline" onClick={() => decisionFn(card.id, "NOT_OK")}>NOT OK</button>
                    <button className="contrast" onClick={() => decisionFn(card.id, "OK")}>OK</button>
                </div>
            </div>
        </section>
    );
}