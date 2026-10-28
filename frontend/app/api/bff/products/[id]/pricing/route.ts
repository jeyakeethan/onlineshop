const BACKEND = process.env.BACKEND_BASE_URL!;

export async function PATCH(
  req: Request,
  context: { params: Promise<{ id: string }> },
) {
  const { id } = await context.params;
  const inventoryId = new URL(req.url).searchParams.get("inventoryId");
  const body = await req.text();
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  if (cookie) headers.cookie = cookie;
  const response = await fetch(
    `${BACKEND}/api/v1/products/${id}/inventory/${inventoryId}/pricing`,
    { method: "PATCH", headers, body },
  );
  return new Response(await response.text(), {
    status: response.status,
    headers: {
      "content-type":
        response.headers.get("content-type") || "application/json",
    },
  });
}
