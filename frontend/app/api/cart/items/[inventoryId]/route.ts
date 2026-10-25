const BACKEND = process.env.BACKEND_BASE_URL!

function buildHeaders(req: Request, contentType?: string) {
  const headers: Record<string, string> = {}
  if (contentType) headers['content-type'] = contentType
  const cookie = req.headers.get('cookie')
  if (cookie) headers.cookie = cookie
  return headers
}

export async function DELETE(req: Request, context: any) {
  const { inventoryId } = context?.params || {}
  const resp = await fetch(`${BACKEND}/api/v1/cart/items/${inventoryId}`, { method: 'DELETE', headers: buildHeaders(req) })
  const text = await resp.text()
  return new Response(text, { status: resp.status })
}

export async function PATCH(req: Request, context: any) {
  const { inventoryId } = context?.params || {}
  const body = await req.text()
  const resp = await fetch(`${BACKEND}/api/v1/cart/items/${inventoryId}`, { method: 'PATCH', headers: buildHeaders(req, 'application/json'), body })
  const text = await resp.text()
  return new Response(text, { status: resp.status })
}
