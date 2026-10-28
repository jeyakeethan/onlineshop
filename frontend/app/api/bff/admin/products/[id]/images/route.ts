const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(
  req: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id: productId } = await params;
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/admin/products/${encodeURIComponent(productId)}/images`,
      {
        method: "POST",
        headers,
        body: await req.formData(),
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
      { message: "Unable to upload product image." },
      { status: 502 },
    );
  }
}
