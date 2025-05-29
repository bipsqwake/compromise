import { useState } from "react";

export default function RoomHello({name, onInput}: {name: string, onInput: (name: string) => void}) {
    const [username, setUsername] = useState("");

    return (
        <section className="connect-container">
            <div className="connect-title">
                Вы подключаетесь к комнате <br/><span style={{color:'#6366f1'}}>{name}</span>
            </div>
            <form className="connect-form">
                <input type="text" placeholder="Ваше имя" value={username} onChange={e => setUsername(e.target.value)} required/>
                    <button type="submit" onClick={() => onInput(username)} className="contrast connect-btn">Подключиться</button>
            </form>
        </section>
    );
}