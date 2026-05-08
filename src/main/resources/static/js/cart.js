(function() {
  function getCurrentUserId() {
    return sessionStorage.getItem('currentUserId');
  }

  function getCurrentUserEmail() {
    return sessionStorage.getItem('currentUserEmail');
  }

  function requireLoginForCart() {
    if (getCurrentUserId()) {
      return true;
    }

    alert('Please login to add items to your cart.');
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    window.location.href = `login.html?redirect=${encodeURIComponent(currentPage)}`;
    return false;
  }

  async function addToCart(book) {
    const userId = getCurrentUserId();
    if (!userId) {
      return false;
    }

    const payload = {
      bookId: book.bookId,
      title: book.title,
      author: book.author,
      price: Number(book.price),
      imageUrl: book.imageUrl,
      quantity: Number(book.quantity || 1)
    };

    const response = await fetch(`/api/cart/${encodeURIComponent(userId)}/items`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    const data = await response.json();
    if (!response.ok || data.status !== 'success') {
      throw new Error(data.message || 'Failed to add item to cart');
    }

    if (typeof window.updateNavbarCartSummary === 'function') {
      await window.updateNavbarCartSummary();
    }

    return data;
  }

  async function getCart() {
    const userId = getCurrentUserId();
    if (!userId) {
      return null;
    }

    const response = await fetch(`/api/cart/${encodeURIComponent(userId)}`);
    const data = await response.json();

    if (!response.ok || data.status !== 'success') {
      throw new Error(data.message || 'Failed to load cart');
    }

    return data;
  }

  async function updateCartItem(bookId, quantity) {
    const userId = getCurrentUserId();
    if (!userId) {
      throw new Error('Please login to update cart');
    }

    const response = await fetch(`/api/cart/${encodeURIComponent(userId)}/items/${encodeURIComponent(bookId)}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ quantity: Number(quantity) })
    });

    const data = await response.json();
    if (!response.ok || data.status !== 'success') {
      throw new Error(data.message || 'Failed to update cart');
    }

    if (typeof window.updateNavbarCartSummary === 'function') {
      await window.updateNavbarCartSummary();
    }

    return data;
  }

  async function removeCartItem(bookId) {
    const userId = getCurrentUserId();
    if (!userId) {
      throw new Error('Please login to update cart');
    }

    const response = await fetch(`/api/cart/${encodeURIComponent(userId)}/items/${encodeURIComponent(bookId)}`, {
      method: 'DELETE'
    });

    const data = await response.json();
    if (!response.ok || data.status !== 'success') {
      throw new Error(data.message || 'Failed to remove cart item');
    }

    if (typeof window.updateNavbarCartSummary === 'function') {
      await window.updateNavbarCartSummary();
    }

    return data;
  }

  window.BookstoreCart = {
    getCurrentUserId,
    getCurrentUserEmail,
    requireLoginForCart,
    addToCart,
    getCart,
    updateCartItem,
    removeCartItem
  };
})();
