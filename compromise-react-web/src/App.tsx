import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { CompromiseRoomApiClient } from "./api/CompromiseRoomApiClient";
import CompromiseRoomApiContext from "./api/CompromiseRoomApiContext";
import AppRouter from "./AppRouter";

export default function App() {
    const queryClient = new QueryClient()

    return (
        <QueryClientProvider client={queryClient}>
            <CompromiseRoomApiContext.Provider value={new CompromiseRoomApiClient("http://localhost:8081")}>
                <AppRouter />
            </CompromiseRoomApiContext.Provider>
        </QueryClientProvider>

    );
}