export const BACKEND = process.env.BACKEND_BASE_URL!;

export async function proxyGet(path: string) {
  const res = await fetch(path);
  if (!res.ok) throw new Error("Fetch error");
  return res.json();
}
