import type { Route } from "./+types/Hello";

export async function clientLoader({
  params,
}: Route.ClientLoaderArgs) {
  const product = "asdasd"
  return product;
}

// HydrateFallback is rendered while the client loader is running
export function HydrateFallback() {
  return <div>Loading...</div>;
}

export default function Product({
  loaderData,
}: Route.ComponentProps) {
  const name = loaderData;
  return (
    <div>
      <h1>{name}</h1>
    </div>
  );
}

