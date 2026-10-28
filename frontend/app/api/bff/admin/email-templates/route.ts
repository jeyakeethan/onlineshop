const BACKEND = process.env.BACKEND_BASE_URL!;
export async function GET(req: Request) {
  const incoming = new URL(req.url);
  const query = new URLSearchParams({
    page: incoming.searchParams.get("page") || "0",
    size: incoming.searchParams.get("size") || "20",
  });
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/admin/email-templates?${query}`,
      { headers, cache: "no-store" },
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
      { message: "Unable to load email templates." },
      { status: 502 },
    );
  }
}
