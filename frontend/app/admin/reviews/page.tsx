"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Review = {
  reviewId?: number;
  userId?: string;
  productId?: number;
  productName?: string;
  reviewText?: string;
  rating?: number;
  moderationStatus?: string;
  moderationReason?: string;
  moderatedBy?: string;
  moderatedAt?: string;
};
export default function AdminReviewsPage() {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [status, setStatus] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    fetch(
      `/api/bff/admin/reviews${status ? `?status=${encodeURIComponent(status)}` : ""}`,
      { cache: "no-store" },
    )
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access review moderation."
              : r.status === 403
                ? "You do not have permission to moderate reviews."
                : d?.message || "Unable to load reviews.",
          );
        setReviews(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load reviews."),
      )
      .finally(() => setLoading(false));
  }, [status]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading reviews...</div>
      </div>
    );
  if (error)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Access restricted</h1>
          <p>{error}</p>
          <Link href="/auth/login" className="primary-button">
            Sign in
          </Link>
        </div>
      </div>
    );
  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Content moderation</p>
          <h1>Reviews</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Catalog
        </Link>
      </div>
      <label>
        Status
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">All</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
          <option value="HIDDEN">Hidden</option>
        </select>
      </label>
      {reviews.length ? (
        <div className="inventory-list">
          {reviews.map((review) => (
            <article className="inventory-product" key={review.reviewId}>
              <h2>
                {review.productName || `Product #${review.productId}`} ·{" "}
                {"★".repeat(review.rating || 0)}
              </h2>
              <p>{review.reviewText}</p>
              <p className="muted">
                User {review.userId} · {review.moderationStatus || "PENDING"}{" "}
                {review.moderationReason ? `· ${review.moderationReason}` : ""}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No reviews found.</div>
      )}
    </div>
  );
}
