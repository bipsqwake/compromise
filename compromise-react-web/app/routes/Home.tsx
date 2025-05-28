import type { Route } from "./+types/home";
import CompromiseApiContext from "~/restapi/CompromiseApiContext";
import { CompromiseApiClient } from "~/restapi/Api";
import { Welcome } from "~/welcome/welcome";

export function meta({ }: Route.MetaArgs) {
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
