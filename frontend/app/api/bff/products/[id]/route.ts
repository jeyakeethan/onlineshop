const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET(req: Request, context: any) {
  const { id } = context?.params || {};
  const backendUrl = `${BACKEND}/api/v1/products/${id}`;
  const resp = await fetch(backendUrl);
  const body = await resp.text();
  return new Response(body, {
    status: resp.status,
    headers: {
      "content-type": resp.headers.get("content-type") || "application/json",
    },
  });
}

export async function PUT(req: Request, context: any) {
  const { id } = context?.params || {};
  const body = await req.text();
  const backendUrl = `${BACKEND}/api/v1/products/${id}`;
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  if (cookie) headers.cookie = cookie;
  const resp = await fetch(backendUrl, { method: "PUT", headers, body });
  const text = await resp.text();
  return new Response(text, { status: resp.status });
}

export async function DELETE(req: Request, context: any) {
  const { id } = context?.params || {};
  const backendUrl = `${BACKEND}/api/v1/products/${id}`;
  const resp = await fetch(backendUrl, { method: "DELETE" });
  const text = await resp.text();
  return new Response(text, { status: resp.status });
}
