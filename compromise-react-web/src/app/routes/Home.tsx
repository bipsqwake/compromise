export default function Home() {

    function goToQuickRooms() {
        window.location.replace(`/quick`);
    }

    return (
        <div>
            <header>
                <h1>Synkro</h1>
                <hr />
            </header>
            <p className="slogan">Всем нравится? Тогда берём!</p>
            <div className="button-group">
                {/* <a href="#" role="button" className="contrast">Войти</a> */}
                <a href="#" onClick={goToQuickRooms} role="button">Быстрые голосования</a>
            </div>
        </div>
    );
}