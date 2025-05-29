import { BrowserRouter, Route, Routes } from "react-router";
import Home from "./app/routes/Home";
import './App.css'
import Room from "./app/routes/Room";

export default function AppRouter() {


    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/rooms/:roomId" element={<Room />} />
            </Routes>
        </BrowserRouter>
    );
}