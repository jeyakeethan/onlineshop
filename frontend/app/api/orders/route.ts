const BACKEND = process.env.BACKEND_BASE_URL!

function buildHeaders(req: Request) {
  const headers: Record<string, string> = {}
  const ct = req.headers.get('content-type')
  if (ct) headers['content-type'] = ct
  const cookie = req.headers.get('cookie')
  if (cookie) headers['cookie'] = cookie
  return headers
}

async function proxy(req: Request) {
  const url = new URL(req.url)
  const tail = url.pathname.replace('/api/orders', '')
  const backendPath = tail === '/admin' ? '/api/v1/admin/orders' : `/api/v1/orders${tail}`
  const backendUrl = `${BACKEND}${backendPath}${url.search}`
  const method = req.method
  const init: any = { method, headers: buildHeaders(req) }
  if (method !== 'GET' && method !== 'HEAD') {
    const buf = await req.arrayBuffer()
    init.body = Buffer.from(buf) as any
  }
  const resp = await fetch(backendUrl, init)
  const isBinary = resp.headers.get('content-type')?.startsWith('image/')
  if (isBinary) {
    const ab = await resp.arrayBuffer()
    return new Response(ab, { status: resp.status, headers: { 'content-type': resp.headers.get('content-type') || '' } })
  }
  const text = await resp.text()
  return new Response(text, { status: resp.status, headers: { 'content-type': resp.headers.get('content-type') || 'application/json' } })
}

export const GET = proxy
export const POST = proxy
export const DELETE = proxy
export const PATCH = proxy
