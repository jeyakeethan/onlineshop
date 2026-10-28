const BACKEND = process.env.BACKEND_BASE_URL!;

async function proxy(
  req: Request,
  context: { params: Promise<{ path: string[] }> },
) {
  const { path } = await context.params;
  const url = new URL(req.url);
  const backendPath =
    path[0] === "admin"
      ? `/api/v1/admin/orders/${path.slice(1).join("/")}`
      : `/api/v1/orders/${path.join("/")}`;
  const backendUrl = `${BACKEND}${backendPath}${url.search}`;
  const headers: Record<string, string> = {};
  const contentType = req.headers.get("content-type");
  const cookie = req.headers.get("cookie");
  if (contentType) headers["content-type"] = contentType;
  if (cookie) headers.cookie = cookie;
  const init: RequestInit = { method: req.method, headers };
  if (req.method !== "GET" && req.method !== "HEAD")
    init.body = await req.arrayBuffer();
  const response = await fetch(backendUrl, init);
  const body = await response.text();
  return new Response(body, {
    status: response.status,
    headers: {
      "content-type":
        response.headers.get("content-type") || "application/json",
    },
  });
}

export const GET = proxy;
export const POST = proxy;
export const DELETE = proxy;
export const PATCH = proxy;
