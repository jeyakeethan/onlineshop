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

async function proxy(
  req: Request,
  context: { params: Promise<{ addressId: string }> },
) {
  const { addressId } = await context.params;
  const response = await fetch(`${BACKEND}/api/v1/me/addresses/${addressId}`, {
    method: req.method,
    headers: headers(
      req,
      req.method === "PATCH" ? "application/json" : undefined,
    ),
    body: req.method === "PATCH" ? await req.text() : undefined,
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

export const PATCH = proxy;
export const DELETE = proxy;

export async function PUT(
  req: Request,
  context: { params: Promise<{ addressId: string }> },
) {
  const { addressId } = await context.params;
  const response = await fetch(
    `${BACKEND}/api/v1/me/addresses/${addressId}/default`,
    { method: "PUT", headers: headers(req), cache: "no-store" },
  );
  return new Response(await response.text(), {
    status: response.status,
    headers: {
      "content-type":
        response.headers.get("content-type") || "application/json",
    },
  });
}
