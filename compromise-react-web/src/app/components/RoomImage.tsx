export default function RoomImage({ url }: { url: string }) {
    return (
        <div className="image-container">
            <img src={url} alt="Случайное изображение" className="process-image" />
        </div>
    );
}