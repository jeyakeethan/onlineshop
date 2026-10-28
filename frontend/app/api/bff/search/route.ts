const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET(req: Request) {
  const query = new URL(req.url).searchParams.get("q")?.trim() || "";
  if (!query)
    return Response.json(
      { message: "Search term is required." },
      { status: 400 },
    );
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/search?q=${encodeURIComponent(query)}`,
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
      { message: "Unable to search products." },
      { status: 502 },
    );
  }
}
