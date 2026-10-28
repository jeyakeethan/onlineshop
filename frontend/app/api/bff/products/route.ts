const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET(req: Request) {
  const url = new URL(req.url);
  const search = url.search;
  const backendUrl = `${BACKEND}/api/v1/products${search}`;
  const resp = await fetch(backendUrl);
  const body = await resp.text();
  const headers = new Headers();
  headers.set(
    "content-type",
    resp.headers.get("content-type") || "application/json",
  );
  return new Response(body, { status: resp.status, headers });
}

export async function POST(req: Request) {
  const body = await req.text();
  const backendUrl = `${BACKEND}/api/v1/products`;
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  if (cookie) headers.cookie = cookie;
  const resp = await fetch(backendUrl, { method: "POST", headers, body });
  const text = await resp.text();
  return new Response(text, { status: resp.status });
}
