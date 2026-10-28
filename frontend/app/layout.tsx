import "../styles/globals.css";
import React from "react";
import { Footer, Header } from "../components/storefront";
import { isAuthenticated } from "../lib/auth";

export const metadata = {
  title: "Online Shop",
  description: "Next.js front-end for Online Shop",
};

export default async function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const authenticated = await isAuthenticated();

  return (
    <html lang="en">
      <body>
        <Header isAuthenticated={authenticated} />
        <main>{children}</main>
        <Footer />
      </body>
    </html>
  );
}
