const BACKEND = process.env.BACKEND_BASE_URL!;
export async function GET(
  req: Request,
  { params }: { params: Promise<{ slug: string }> },
) {
  const { slug } = await params;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/pages/${encodeURIComponent(slug)}`,
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
    return Response.json({ message: "Unable to load page." }, { status: 502 });
  }
}
