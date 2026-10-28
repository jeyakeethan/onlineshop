/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  async rewrites() {
    const backend = process.env.BACKEND_BASE_URL;
    if (!backend) throw new Error("BACKEND_BASE_URL is required");
    return [
      { source: "/api/v1/:path*", destination: `${backend}/api/v1/:path*` },
    ];
  },
  // appDir is enabled by default in recent Next.js versions when using the App Router
};
module.exports = nextConfig;
