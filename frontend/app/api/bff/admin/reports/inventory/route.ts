const BACKEND = process.env.BACKEND_BASE_URL!;
export async function GET(req: Request) {
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(`${BACKEND}/api/v1/admin/reports/inventory`, {
      headers,
      cache: "no-store",
    });
    return new Response(await response.arrayBuffer(), {
      status: response.status,
      headers: {
        "content-type": response.headers.get("content-type") || "text/csv",
        "content-disposition":
          response.headers.get("content-disposition") ||
          "attachment; filename=inventory.csv",
        "cache-control": "no-store",
      },
    });
  } catch {
    return Response.json(
      { message: "Unable to download inventory report." },
      { status: 502 },
    );
  }
}
