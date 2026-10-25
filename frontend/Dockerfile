FROM node:20-alpine AS dependencies

WORKDIR /app
RUN corepack enable

COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

FROM node:20-alpine AS builder

WORKDIR /app
RUN corepack enable

COPY --from=dependencies /app/node_modules ./node_modules
COPY . .

ENV BACKEND_BASE_URL=http://app:8080
RUN pnpm build

FROM node:20-alpine AS runner

WORKDIR /app
ENV NODE_ENV=production
ENV BACKEND_BASE_URL=http://app:8080

RUN corepack enable
COPY --from=builder /app .

EXPOSE 3000

CMD ["pnpm", "start"]