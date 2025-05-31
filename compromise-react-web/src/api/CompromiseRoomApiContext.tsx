import { createContext } from "react";
import { CompromiseRoomApiClient } from "./CompromiseRoomApiClient";

const CompromiseRoomApiContext = createContext<CompromiseRoomApiClient>(new CompromiseRoomApiClient(import.meta.env.VITE_API_URL))
export default CompromiseRoomApiContext;