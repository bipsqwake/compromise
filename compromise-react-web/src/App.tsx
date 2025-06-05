import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import 'react-toastify/dist/ReactToastify.css';
import { CompromiseRoomApiClient } from "./api/CompromiseRoomApiClient";
import CompromiseRoomApiContext from "./api/CompromiseRoomApiContext";
import AppRouter from "./AppRouter";
import { Toaster } from "react-hot-toast";

export default function App() {
    const queryClient = new QueryClient()

    return (
        <QueryClientProvider client={queryClient}>
            <CompromiseRoomApiContext.Provider value={new CompromiseRoomApiClient(import.meta.env.VITE_API_URL)}>
                <AppRouter />

                <Toaster
                    position="top-right"
                    reverseOrder={false}
                    gutter={8}
                    containerClassName=""
                    containerStyle={{}}
                    toastOptions={{
                        // Define default options
                        className: '',
                        duration: 3000,
                        removeDelay: 1000,
                        style: {
                            background: 'white',
                            color: 'black',
                        }
                    }}
                />
            </CompromiseRoomApiContext.Provider>
        </QueryClientProvider>

    );
}