import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { CompromiseRoomApiClient } from "./api/CompromiseRoomApiClient";
import CompromiseRoomApiContext from "./api/CompromiseRoomApiContext";
import AppRouter from "./AppRouter";

export default function App() {
    const queryClient = new QueryClient()

    return (
        <QueryClientProvider client={queryClient}>
            <CompromiseRoomApiContext.Provider value={new CompromiseRoomApiClient(import.meta.env.VITE_API_URL)}>
                <AppRouter />
            </CompromiseRoomApiContext.Provider>
        </QueryClientProvider>

    );
}