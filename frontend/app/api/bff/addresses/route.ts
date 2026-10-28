const BACKEND = process.env.BACKEND_BASE_URL!;

function headers(req: Request, contentType?: string) {
  const result: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) result.cookie = cookie;
  if (authorization) result.authorization = authorization;
  if (contentType) result["content-type"] = contentType;
  return result;
}

export async function GET(req: Request) {
  const response = await fetch(`${BACKEND}/api/v1/me/addresses`, {
    headers: headers(req),
    cache: "no-store",
  });
  return new Response(await response.text(), {
    status: response.status,
    headers: {
      "content-type":
        response.headers.get("content-type") || "application/json",
      "cache-control": "no-store",
    },
  });
}

export async function POST(req: Request) {
  const response = await fetch(`${BACKEND}/api/v1/me/addresses`, {
    method: "POST",
    headers: headers(req, "application/json"),
    body: await req.text(),
    cache: "no-store",
  });
  return new Response(await response.text(), {
    status: response.status,
    headers: {
      "content-type":
        response.headers.get("content-type") || "application/json",
    },
  });
}
