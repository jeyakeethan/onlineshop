const BACKEND = process.env.BACKEND_BASE_URL!;

export async function POST(
  req: Request,
  context: { params: Promise<{ skuId: string }> },
) {
  const { skuId } = await context.params;
  const body = await req.text();
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  if (cookie) headers.cookie = cookie;
  const response = await fetch(
    `${BACKEND}/api/v1/products/inventory/${skuId}`,
    {
      method: "POST",
      headers,
      body,
    },
  );
  return new Response(await response.text(), {
    status: response.status,
    headers: {
      "content-type":
        response.headers.get("content-type") || "application/json",
    },
  });
}
