const BACKEND = process.env.BACKEND_BASE_URL!;

function buildHeaders(req: Request) {
  const headers: Record<string, string> = {};
  const ct = req.headers.get("content-type");
  if (ct) headers["content-type"] = ct;
  const cookie = req.headers.get("cookie");
  if (cookie) headers["cookie"] = cookie;
  return headers;
}

export async function GET(req: Request) {
  const url = new URL(req.url);
  const id = url.pathname.split("/").pop();
  const resp = await fetch(`${BACKEND}/api/v1/images/${id}`);
  const arrayBuffer = await resp.arrayBuffer();
  const headers: Record<string, string> = {};
  const contentType = resp.headers.get("content-type");
  if (contentType) headers["content-type"] = contentType;
  return new Response(arrayBuffer, { status: resp.status, headers });
}

export async function POST(req: Request) {
  const backendUrl = `${BACKEND}/api/v1/images/upload`;
  const contentType = req.headers.get("content-type") || "";
  const arr = await req.arrayBuffer();
  const resp = await fetch(backendUrl, {
    method: "POST",
    body: Buffer.from(arr) as any,
    headers: buildHeaders(req),
  });
  const text = await resp.text();
  return new Response(text, { status: resp.status });
}
