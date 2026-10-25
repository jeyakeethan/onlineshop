const BACKEND = process.env.BACKEND_BASE_URL!

export async function POST(req: Request) {
  const body = await req.json()
  const resp = await fetch(`${BACKEND}/api/v1/auth/login`, { method: 'POST', body: JSON.stringify(body), headers: { 'content-type': 'application/json' } })
  const data = await resp.json()
  // backend returns { token }
  if (resp.ok && data?.token) {
    const headers = new Headers()
    // set HttpOnly cookie
    headers.append('Set-Cookie', `token=${data.token}; HttpOnly; Path=/; SameSite=Lax`)
    return new Response(JSON.stringify({ ok: true }), { status: 200, headers })
  }
  return new Response(JSON.stringify(data), { status: resp.status })
}
