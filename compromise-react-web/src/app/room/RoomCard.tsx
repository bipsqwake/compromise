import TinderCard from "react-tinder-card";
import type { Card } from "../../types/ws/Card";
import type { Decision } from "../../types/ws/DecisionMessage";
import { useRef } from "react";

export default function RoomCard({ card, decisionFn }: { card: Card, decisionFn: (arg1: string, arg2: Decision) => void }) {

    const decision = useRef("");

    const swiped = (direction: string, nameToDelete: string) => {
        console.log('removing: ' + nameToDelete + " " + direction)
        if (direction == "right" || direction == "up") {
            decision.current = "OK";
        } else if (direction == "left" || direction == "down") {
            decision.current = "NOT_OK";
        } else {
            console.log("hmm " + (direction == "right"));
        }
    }

    const outOfFrame = () => {
        console.log("Decide " + decision.current)
        decisionFn(card.id, decision.current as Decision);
    }

    const encodedUrl = encodeURI(card.img);

    const style = {
        backgroundImage: `url("${encodedUrl}")`,
        backgroundSize: "contain"
    }
    return (
        <TinderCard preventSwipe={["up", "down"]} className='swipe' onSwipe={(dir) => swiped(dir, card.id)} onCardLeftScreen={() => outOfFrame()}>
            <div className="card" style={style}>
                <div className="card-content">
                    <div className="card-title">{card.name}</div>
                    <div className="card-desc">{card.description}</div>
                </div>
            </div>
        </TinderCard>
    );
}