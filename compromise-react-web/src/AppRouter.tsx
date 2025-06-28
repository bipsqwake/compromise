import { BrowserRouter, Route, Routes } from "react-router";
import Home from "./app/routes/Home";
import './App.css'
import Room from "./app/routes/Room";
import BgPrepare from "./app/routes/BgPrepare";
import QuickRooms from "./app/routes/QuickRooms";
import MoviePrepare from "./app/routes/MoviePrepare";

export default function AppRouter() {


    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/quick" element={<QuickRooms />} />
                <Route path="/rooms/:roomId" element={<Room />} />
                <Route path="/prepare/boardgame" element={<BgPrepare />} />
                <Route path="/prepare/movie" element={<MoviePrepare />} />
            </Routes>
        </BrowserRouter>
    );
}