export default function HomeButton() {

    function goHome() {
        window.location.replace(`/`)
    }

    return (
        <a href="#" onClick={goHome} role="button">В начало</a>
    );
}