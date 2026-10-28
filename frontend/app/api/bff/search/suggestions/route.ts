const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET(req: Request) {
  const query = new URL(req.url).searchParams.get("query")?.trim() || "";
  if (query.length < 2) return Response.json([]);
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/products/search/suggestions?query=${encodeURIComponent(query)}`,
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
      { message: "Unable to load suggestions." },
      { status: 502 },
    );
  }
}
