import HomeButton from "../components/HomeButton";
import RoomImage from "../components/RoomImage";

export default function Stub({img, header, title, desc}: {img: string, header: string, title: string, desc: string}) {
    return (
        <section className="process-container">
            <h1>{header}</h1>
            <div className="room-content">
                <RoomImage url={img} />
                <div className="room-title">{title}</div>
                <div className="room-desc">{desc}</div>
            </div>
            <HomeButton />
        </section>
    );
}