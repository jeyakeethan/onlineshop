import Link from "next/link";
type Page = { title?: string; content?: string; publishedAt?: string };
export default async function PublicContentPage({
  params,
}: {
  params: Promise<{ slug: string }>;
}) {
  const { slug } = await params;
  const response = await fetch(
    `${process.env.BACKEND_BASE_URL}/api/v1/pages/${encodeURIComponent(slug)}`,
    { cache: "no-store" },
  );
  if (!response.ok)
    return (
      <div className="page-container">
        <div className="empty">
          <h1>Page unavailable</h1>
          <Link href="/" className="primary-button">
            Back home
          </Link>
        </div>
      </div>
    );
  const page = (await response.json()) as Page;
  return (
    <main className="page-container">
      <Link href="/" className="back-link">
        Back home
      </Link>
      <article className="form-shell mt-6">
        <p className="eyebrow">Information</p>
        <h1>{page.title}</h1>
        <div className="content-body">{page.content}</div>
        {page.publishedAt && (
          <p className="muted">Published {page.publishedAt}</p>
        )}
      </article>
    </main>
  );
}
