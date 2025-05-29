import { createContext } from "react";
import { CompromiseRoomApiClient } from "./CompromiseRoomApiClient";

const CompromiseRoomApiContext = createContext<CompromiseRoomApiClient>(new CompromiseRoomApiClient("http://localhost:8081"))
export default CompromiseRoomApiContext;