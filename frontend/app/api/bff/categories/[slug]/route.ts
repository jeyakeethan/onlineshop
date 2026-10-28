const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET(
  _req: Request,
  context: { params: Promise<{ slug: string }> },
) {
  const { slug } = await context.params;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/categories/${encodeURIComponent(slug)}`,
      { cache: "no-store" },
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
      { message: "Unable to load category." },
      { status: 502 },
    );
  }
}
