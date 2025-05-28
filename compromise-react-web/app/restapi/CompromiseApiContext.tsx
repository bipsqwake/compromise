import { createContext } from "react";
import { CompromiseApiClient } from "./Api";

const CompromiseApiContext = createContext<CompromiseApiClient>(new CompromiseApiClient("http://localhost:8081"))
export default CompromiseApiContext