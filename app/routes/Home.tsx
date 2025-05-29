
import CompromiseApiContext from "~/restapi/CompromiseApiContext";
import { CompromiseApiClient } from "~/restapi/Api";
import type * as root from "../+types/root";
import { Welcome } from "~/welcome/Welcome";

export function meta({ }: root.Route.MetaArgs) {
  return [
    { title: "Home | Compromise" }
  ];
}

export default function Home() {
  return (
    <CompromiseApiContext.Provider value={new CompromiseApiClient("http://localhost:8081")}>
      <Welcome />
    </CompromiseApiContext.Provider>

  );
}
