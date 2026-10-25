const BACKEND = process.env.BACKEND_BASE_URL!

export async function POST(req: Request) {
  const body = await req.json()
  const payload = {
    email: body.email,
    firstName: body.firstName,
    lastName: body.lastName,
    password: body.password,
    dob: body.dob || null,
    roles: ['BUYER'],
  }

  const resp = await fetch(`${BACKEND}/api/v1/users`, {
    method: 'POST',
    body: JSON.stringify(payload),
    headers: { 'content-type': 'application/json' },
  })

  const contentType = resp.headers.get('content-type') || ''
  const isJson = contentType.includes('application/json')
  const data = isJson ? await resp.json() : await resp.text()

  if (resp.ok) {
    const token = typeof data === 'string' ? data : data?.token
    const headers = new Headers()
    if (token) {
      headers.append('Set-Cookie', `token=${token}; HttpOnly; Path=/; SameSite=Lax`)
    }
    return new Response(JSON.stringify({ ok: true }), { status: 200, headers })
  }

  return new Response(JSON.stringify(typeof data === 'string' ? { message: data } : data), { status: resp.status })
}
