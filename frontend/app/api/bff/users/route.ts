const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET(req: Request) {
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;

  const res = await fetch(`${BACKEND}/api/v1/me`, {
    headers,
    cache: "no-store",
  });
  const text = await res.text();
  return new Response(text, {
    status: res.status,
    headers: {
      "content-type": res.headers.get("content-type") || "application/json",
      "cache-control": "no-store",
    },
  });
}

export async function PATCH(req: Request) {
  const body = await req.text();
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  if (cookie) headers.cookie = cookie;
  const res = await fetch(`${BACKEND}/api/v1/me`, {
    method: "PATCH",
    headers,
    body,
    cache: "no-store",
  });
  const text = await res.text();
  return new Response(text, {
    status: res.status,
    headers: {
      "content-type": res.headers.get("content-type") || "application/json",
      "cache-control": "no-store",
    },
  });
}
