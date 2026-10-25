const BACKEND = process.env.BACKEND_BASE_URL!

export async function GET(req: Request) {
  const res = await fetch(`${BACKEND}/api/v1/users/me`, { headers: req.headers })
  const text = await res.text()
  return new Response(text, { status: res.status, headers: { 'content-type': res.headers.get('content-type') || 'application/json' } })
}

export async function PUT(req: Request) {
  const body = await req.text()
  const headers: Record<string, string> = { 'content-type': 'application/json' }
  const cookie = req.headers.get('cookie')
  if (cookie) headers.cookie = cookie
  const res = await fetch(`${BACKEND}/api/v1/users/me`, { method: 'PUT', headers, body })
  const text = await res.text()
  return new Response(text, { status: res.status })
}
