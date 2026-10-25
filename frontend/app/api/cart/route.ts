const BACKEND = process.env.BACKEND_BASE_URL!

function buildHeaders(req: Request) {
  const headers: Record<string, string> = {}
  const ct = req.headers.get('content-type')
  if (ct) headers['content-type'] = ct
  const cookie = req.headers.get('cookie')
  if (cookie) headers['cookie'] = cookie
  return headers
}
 

export async function GET(req: Request) {
  const resp = await fetch(`${BACKEND}/api/v1/cart`, { headers: req.headers })
  const body = await resp.text()
  return new Response(body, { status: resp.status, headers: { 'content-type': resp.headers.get('content-type') || 'application/json' } })
}

export async function POST(req: Request) {
  const body = await req.text()
  const resp = await fetch(`${BACKEND}/api/v1/cart/items`, { method: 'POST', headers: buildHeaders(req), body })
  const text = await resp.text()
  return new Response(text, { status: resp.status })
}

export async function DELETE(req: Request) {
  // support clearing cart
  const url = new URL(req.url)
  const pathname = url.pathname
  if (pathname.endsWith('/api/cart')) {
    const resp = await fetch(`${BACKEND}/api/v1/cart`, { method: 'DELETE', headers: buildHeaders(req) })
    const text = await resp.text()
    return new Response(text, { status: resp.status })
  }
  return new Response(null, { status: 405 })
}

// PATCH for cart-level updates is not used; item-level PATCH handled in /api/cart/items/[inventoryId]
