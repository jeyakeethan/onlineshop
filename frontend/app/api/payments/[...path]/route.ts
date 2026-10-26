const BACKEND = process.env.BACKEND_BASE_URL!

function buildHeaders(req: Request) {
  const headers: Record<string, string> = {}
  const contentType = req.headers.get('content-type')
  const cookie = req.headers.get('cookie')
  const idempotencyKey = req.headers.get('Idempotency-Key')
  if (contentType) headers['content-type'] = contentType
  if (cookie) headers.cookie = cookie
  if (idempotencyKey) headers['Idempotency-Key'] = idempotencyKey
  return headers
}

async function proxy(req: Request) {
  const url = new URL(req.url)
  const tail = url.pathname.replace('/api/payments', '')
  const init: RequestInit = { method: req.method, headers: buildHeaders(req) }
  if (req.method !== 'GET' && req.method !== 'HEAD') init.body = await req.arrayBuffer()
  const response = await fetch(`${BACKEND}/api/v1/payments${tail}${url.search}`, init)
  return new Response(await response.text(), {
    status: response.status,
    headers: { 'content-type': response.headers.get('content-type') || 'application/json' }
  })
}

export const POST = proxy
