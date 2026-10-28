const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET(
  _req: Request,
  context: { params: Promise<{ id: string }> },
) {
  const { id } = await context.params;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/products/${encodeURIComponent(id)}/related`,
      { cache: "no-store" },
    );
    return new Response(await response.text(), {
      status: response.status,
      headers: {
        "content-type": "application/json",
        "cache-control": "no-store",
      },
    });
  } catch {
    return Response.json(
      { message: "Unable to load related products." },
      { status: 502 },
    );
  }
}
