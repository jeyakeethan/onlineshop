const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET() {
  try {
    const response = await fetch(`${BACKEND}/api/v1/homepage`, {
      cache: "no-store",
    });
    return new Response(await response.text(), {
      status: response.status,
      headers: {
        "content-type": "application/json",
        "cache-control": "no-store",
      },
    });
  } catch {
    return Response.json(
      { message: "Unable to load homepage content." },
      { status: 502 },
    );
  }
}
