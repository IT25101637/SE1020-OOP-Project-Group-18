(function () {
  const params = new URLSearchParams(window.location.search);
  const bookId = params.get('bookId');
  const userId = sessionStorage.getItem('currentUserId');

  function stars(rating) {
    const full = '★'.repeat(Math.max(0, Math.min(5, rating)));
    const empty = '☆'.repeat(5 - Math.max(0, Math.min(5, rating)));
    return `<span style="color:#f0b429;">${full}${empty}</span>`;
  }

  async function loadBook() {
    if (!bookId) {
      document.getElementById('bookTitle').textContent = 'Book not found';
      return;
    }

    const response = await fetch(`/api/books/${encodeURIComponent(bookId)}`);
    const data = await response.json();
    if (!response.ok || data.status !== 'success') {
      document.getElementById('bookTitle').textContent = 'Book not found';
      return;
    }

    const book = data.book;
    document.getElementById('bookTitle').textContent = book.title;
    document.getElementById('bookAuthor').textContent = `By ${book.author}`;
    document.getElementById('bookCategory').textContent = `Category: ${book.category}`;
    document.getElementById('bookPrice').textContent = `$${Number(book.price).toFixed(2)}`;
    document.getElementById('bookDescription').textContent = book.description || 'No description available.';
    document.getElementById('bookImage').src = book.imageUrl || 'images/product-placeholder.jpg';
  }

  async function loadReviews() {
    const list = document.getElementById('bookReviewsList');
    const response = await fetch(`/api/reviews/book/${encodeURIComponent(bookId)}`);
    const data = await response.json();

    if (!response.ok || data.status !== 'success') {
      list.innerHTML = '<div class="alert alert-warning">Could not load reviews.</div>';
      return;
    }

    if (!data.reviews || data.reviews.length === 0) {
      list.innerHTML = '<div class="text-muted">No reviews yet. Be the first reviewer.</div>';
      return;
    }

    list.innerHTML = data.reviews.map(review => {
      const mine = userId && review.userId === userId;
      return `
        <div class="border rounded p-3 mb-3 bg-white">
          <div class="d-flex justify-content-between">
            <strong>${review.username || 'Anonymous'}</strong>
            <span>${stars(Number(review.rating || 0))}</span>
          </div>
          <div class="small text-muted mb-2">${new Date(review.updatedAt).toLocaleString()}</div>
          <div>${review.comment}</div>
          ${mine ? `<div class="mt-2"><button class="btn btn-sm btn-outline-secondary" onclick="BookDetails.editReview('${review.reviewId}', ${review.rating}, ${JSON.stringify(review.comment)})">Edit</button> <button class="btn btn-sm btn-outline-danger" onclick="BookDetails.deleteReview('${review.reviewId}')">Delete</button></div>` : ''}
        </div>
      `;
    }).join('');
  }

  async function checkEligibilityAndBindReviewAction() {
    const btn = document.getElementById('reviewActionBtn');
    const msg = document.getElementById('reviewEligibilityMsg');

    if (!userId) {
      msg.textContent = 'Login to write a review.';
      btn.style.display = 'none';
      return;
    }

    const response = await fetch(`/api/payments/user/${encodeURIComponent(userId)}/books/${encodeURIComponent(bookId)}/purchased`);
    const data = await response.json();

    if (response.ok && data.status === 'success' && data.purchased) {
      btn.style.display = 'inline-block';
      btn.onclick = () => addOrUpdateOwnReview();
      msg.textContent = 'You purchased this book. You can review it.';
    } else {
      btn.style.display = 'none';
      msg.textContent = 'Only users who purchased this book can add a review.';
    }
  }

  async function addOrUpdateOwnReview() {
    if (!userId) return;
    const reviewsRes = await fetch(`/api/reviews/user/${encodeURIComponent(userId)}`);
    const reviewsData = await reviewsRes.json();
    const existing = reviewsData.status === 'success' ? (reviewsData.reviews || []).find(r => r.bookId === bookId) : null;

    if (existing) {
      // Edit existing review
      window.location.href = `review.html?bookId=${encodeURIComponent(bookId)}&reviewId=${encodeURIComponent(existing.reviewId)}`;
    } else {
      // Create new review
      window.location.href = `review.html?bookId=${encodeURIComponent(bookId)}`;
    }
  }

  async function editReview(reviewId, oldRating, oldComment) {
    if (!userId) return;
    window.location.href = `review.html?bookId=${encodeURIComponent(bookId)}&reviewId=${encodeURIComponent(reviewId)}`;
  }

  async function deleteReview(reviewId) {
    if (!userId) return;
    if (!confirm('Delete this review?')) return;

    const response = await fetch(`/api/reviews/user/${encodeURIComponent(userId)}/${encodeURIComponent(reviewId)}`, {
      method: 'DELETE'
    });
    const data = await response.json();
    if (!response.ok || data.status !== 'success') {
      alert(data.message || 'Failed to delete review');
      return;
    }
    await loadReviews();
  }

  window.BookDetails = {
    editReview,
    deleteReview
  };

  document.addEventListener('DOMContentLoaded', async () => {
    await loadBook();
    await checkEligibilityAndBindReviewAction();
    await loadReviews();
  });
})();
