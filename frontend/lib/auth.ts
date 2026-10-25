import { cookies } from 'next/headers'

export async function isAuthenticated() {
  const token = (await cookies()).get('token')
  return Boolean(token?.value)
}