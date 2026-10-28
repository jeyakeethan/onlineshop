const BACKEND = process.env.BACKEND_BASE_URL!;

export async function GET(req: Request) {
  const url = new URL(req.url);
  const keyword = url.searchParams.get("keyword") || "";
  const resp = await fetch(
    `${BACKEND}/api/v1/products/search?keyword=${encodeURIComponent(keyword)}`,
  );
  const text = await resp.text();
  return new Response(text, {
    status: resp.status,
    headers: {
      "content-type": resp.headers.get("content-type") || "application/json",
    },
  });
}
