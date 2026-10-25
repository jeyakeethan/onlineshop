# Online Shop Frontend

This is a Next.js App Router TypeScript frontend that proxies requests to the Spring Boot backend via BFF route handlers.

Quick start (from `frontend`):

```bash
npm install
npm run dev
```

Set the required env var `BACKEND_BASE_URL` to point to your Spring Boot server.

Notes:
- BFF routes live under `app/api/*` and proxy to `/api/v1/*` on the backend.
- Server Components are used for product listing and details; interactive UI is client components.
