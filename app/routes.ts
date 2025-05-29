import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
    layout("routes/App.tsx", [
        index("routes/Home.tsx")
    ]),
    route("room/:roomId/hello", "routes/Hello.tsx")
] satisfies RouteConfig;
