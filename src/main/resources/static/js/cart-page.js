document.addEventListener('DOMContentLoaded', async function() {
  if (!window.BookstoreCart || !window.BookstoreCart.getCurrentUserId()) {
    alert('Please login to view your cart.');
    window.location.href = 'login.html?redirect=cart.html';
    return;
  }

  await loadCartPage();

  const continueBtn = document.getElementById('continueShoppingBtn');
  if (continueBtn) {
    continueBtn.addEventListener('click', function() {
      window.location.href = 'index.html';
    });
  }
  
  const checkoutBtn = document.getElementById('checkoutBtn');
  if (checkoutBtn) {
    checkoutBtn.addEventListener('click', function() {
      window.location.href = 'checkout.html';
    });
  }
});

async function loadCartPage() {
  const cartList = document.getElementById('cartItemsList');
  const emptyCart = document.getElementById('emptyCartState');

  try {
    const data = await window.BookstoreCart.getCart();
    const items = data.items || [];

    if (items.length === 0) {
      cartList.innerHTML = '';
      emptyCart.style.display = 'block';
      renderSummary(0, 0);
      return;
    }

    emptyCart.style.display = 'none';
    cartList.innerHTML = items.map(renderCartItem).join('');
    renderSummary(data.totalQuantity || 0, Number(data.subtotal || 0));

    attachCartRowHandlers();
  } catch (error) {
    console.error('Failed to load cart:', error);
    cartList.innerHTML = '<div class="alert alert-danger">Failed to load cart items.</div>';
  }
}

function renderCartItem(item) {
  const safeTitle = escapeHtml(item.title);
  const safeAuthor = escapeHtml(item.author);
  const safeImage = escapeHtml(item.imageUrl);

  return `
    <div class="cart-item-row" data-book-id="${item.bookId}">
      <div class="cart-col item-col">
        <img src="${safeImage}" alt="${safeTitle}" class="cart-book-cover" onerror="this.src='images/product-placeholder.jpg'">
        <div class="cart-item-meta">
          <h5>${safeTitle}</h5>
          <p>Author : ${safeAuthor}</p>
        </div>
      </div>

      <div class="cart-col qty-col">
        <button class="qty-btn qty-minus" type="button" aria-label="Decrease quantity">-</button>
        <span class="qty-value">${item.quantity}</span>
        <button class="qty-btn qty-plus" type="button" aria-label="Increase quantity">+</button>
      </div>

      <div class="cart-col price-col">USD $${Number(item.price).toFixed(2)}</div>
      <div class="cart-col total-col">USD $${Number(item.lineTotal).toFixed(2)}</div>
      <div class="cart-col remove-col">
        <button class="remove-item-btn" type="button" aria-label="Remove item">x</button>
      </div>
    </div>
  `;
}

function renderSummary(totalQuantity, subtotal) {
  document.getElementById('subtotalValue').textContent = `USD $${Number(subtotal).toFixed(2)}`;
  document.getElementById('totalValue').textContent = `USD $${Number(subtotal).toFixed(2)}`;
  document.getElementById('cartCountValue').textContent = totalQuantity;
}

function attachCartRowHandlers() {
  const rows = document.querySelectorAll('.cart-item-row');
  rows.forEach(row => {
    const bookId = row.dataset.bookId;
    const minusBtn = row.querySelector('.qty-minus');
    const plusBtn = row.querySelector('.qty-plus');
    const removeBtn = row.querySelector('.remove-item-btn');
    const qtyValue = row.querySelector('.qty-value');

    plusBtn.addEventListener('click', async function() {
      const nextQty = Number(qtyValue.textContent) + 1;
      await updateQuantity(bookId, nextQty);
    });

    minusBtn.addEventListener('click', async function() {
      const current = Number(qtyValue.textContent);
      const nextQty = current - 1;
      await updateQuantity(bookId, nextQty);
    });

    removeBtn.addEventListener('click', async function() {
      await removeItem(bookId);
    });
  });
}

async function updateQuantity(bookId, quantity) {
  try {
    await window.BookstoreCart.updateCartItem(bookId, quantity);
    await loadCartPage();
  } catch (error) {
    console.error('Failed to update quantity:', error);
    alert(error.message || 'Failed to update quantity');
  }
}

async function removeItem(bookId) {
  try {
    await window.BookstoreCart.removeCartItem(bookId);
    await loadCartPage();
  } catch (error) {
    console.error('Failed to remove item:', error);
    alert(error.message || 'Failed to remove item');
  }
}

function escapeHtml(value) {
  return String(value || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
