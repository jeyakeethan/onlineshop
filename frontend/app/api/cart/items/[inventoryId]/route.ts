const BACKEND = process.env.BACKEND_BASE_URL!;
function headersFor(req: Request, contentType?: string) {
  const headers: Record<string, string> = {};
  if (contentType) headers["content-type"] = contentType;
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  return headers;
}
export async function DELETE(
  req: Request,
  { params }: { params: Promise<{ inventoryId: string }> },
) {
  const { inventoryId } = await params;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/cart/items/${encodeURIComponent(inventoryId)}`,
      { method: "DELETE", headers: headersFor(req), cache: "no-store" },
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
      { message: "Unable to remove cart item." },
      { status: 502 },
    );
  }
}
export async function PATCH(
  req: Request,
  { params }: { params: Promise<{ inventoryId: string }> },
) {
  const { inventoryId } = await params;
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (!Number.isInteger(payload.quantity) || payload.quantity < 1)
      return Response.json(
        { message: "Quantity must be a positive integer." },
        { status: 400 },
      );
  } catch {
    return Response.json({ message: "Invalid cart update." }, { status: 400 });
  }
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/cart/items/${encodeURIComponent(inventoryId)}`,
      {
        method: "PATCH",
        headers: headersFor(req, "application/json"),
        body,
        cache: "no-store",
      },
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
      { message: "Unable to update cart item." },
      { status: 502 },
    );
  }
}
