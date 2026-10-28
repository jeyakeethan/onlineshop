const BACKEND = process.env.BACKEND_BASE_URL!;

export async function PATCH(
  req: Request,
  context: { params: Promise<{ path: string[] }> },
) {
  const { path } = await context.params;
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/admin/categories/${path.map((part) => encodeURIComponent(part)).join("/")}`,
      { method: "PATCH", headers, body: await req.text(), cache: "no-store" },
    );
    return new Response(await response.text(), {
      status: response.status,
      headers: {
        "content-type":
          response.headers.get("content-type") || "application/json",
        "cache-control": "no-store",
      },
    });
  } catch {
    return Response.json(
      { message: "Unable to update category." },
      { status: 502 },
    );
  }
}

export async function DELETE(
  req: Request,
  context: { params: Promise<{ path: string[] }> },
) {
  const { path } = await context.params;
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/admin/categories/${path.map((part) => encodeURIComponent(part)).join("/")}`,
      { method: "DELETE", headers, cache: "no-store" },
    );
    return new Response(await response.text(), {
      status: response.status,
      headers: { "content-type": "text/plain", "cache-control": "no-store" },
    });
  } catch {
    return Response.json(
      { message: "Unable to delete category." },
      { status: 502 },
    );
  }
}
