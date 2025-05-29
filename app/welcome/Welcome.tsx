import { useContext } from "react";
import { Navigate } from "react-router";
import CompromiseApiContext from "~/restapi/CompromiseApiContext";


export function Welcome() {

  const api = useContext(CompromiseApiContext);
  const mutation = api.useCreateRoom();

  function createRoom() {
    mutation.mutate({ name: "Test Room" });
  }

  if (mutation.isError) {
    alert(mutation.error)
    mutation.reset()
  }

  if (mutation.isSuccess) {
    const roomId = mutation.data.data.id
    return <Navigate to={`/room/${roomId}/hello`}/>
  }

  return (
    <main className="container">
      <header>
        <h1>Compromise</h1>
        <hr />
      </header>
      <p className="slogan">Всем нравится? Тогда берём!</p>
      <div className="button-group">
        <a href="#" role="button" className="contrast">Войти</a>
        <a href="#" onClick={createRoom} role="button">Создать комнату</a>
      </div>
    </main>
  );
}
