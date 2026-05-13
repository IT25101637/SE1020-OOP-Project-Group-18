/* ============================================================
   BOOKSAW — CART DRAWER
   Slide-in cart panel. Works with the existing BookstoreCart
   API (cart.js) when a user is logged in, or falls back to
   localStorage for guest sessions.
   ============================================================ */

(function () {
  'use strict';

  /* ── local (guest) cart stored in localStorage ─────────── */
  var GUEST_KEY = 'booksaw-guest-cart';

  function guestCart() {
    try { return JSON.parse(localStorage.getItem(GUEST_KEY)) || []; }
    catch (e) { return []; }
  }

  function saveGuestCart(items) {
    localStorage.setItem(GUEST_KEY, JSON.stringify(items));
  }

  function guestAdd(book) {
    var items = guestCart();
    var existing = items.find(function (i) { return i.bookId === book.bookId; });
    if (existing) {
      existing.quantity += 1;
    } else {
      items.push({ bookId: book.bookId, title: book.title, author: book.author,
                   price: Number(book.price), imageUrl: book.imageUrl, quantity: 1 });
    }
    saveGuestCart(items);
  }

  function guestRemove(bookId) {
    saveGuestCart(guestCart().filter(function (i) { return i.bookId !== bookId; }));
  }

  function guestUpdateQty(bookId, qty) {
    var items = guestCart();
    var item = items.find(function (i) { return i.bookId === bookId; });
    if (item) { item.quantity = qty; }
    saveGuestCart(items);
  }

  function isLoggedIn() {
    return !!(window.BookstoreCart && window.BookstoreCart.getCurrentUserId());
  }

  /* ── inject drawer HTML ────────────────────────────────── */
  function injectDrawer() {
    if (document.getElementById('cart-drawer')) return;

    var html = [
      '<div id="cart-drawer-overlay"></div>',
      '<div id="cart-drawer" aria-label="Shopping cart" role="dialog">',
      '  <div id="cart-drawer-header">',
      '    <span id="cart-drawer-title">🛒 Your Cart</span>',
      '    <button id="cart-drawer-close" aria-label="Close cart">✕</button>',
      '  </div>',
      '  <div id="cart-drawer-body">',
      '    <div id="cart-drawer-empty">Your cart is empty.</div>',
      '    <ul id="cart-drawer-list"></ul>',
      '  </div>',
      '  <div id="cart-drawer-footer">',
      '    <div id="cart-drawer-subtotal">',
      '      <span>Subtotal</span><span id="cart-drawer-total">$0.00</span>',
      '    </div>',
      '    <a href="cart.html" id="cart-drawer-cta">View Cart &amp; Checkout</a>',
      '  </div>',
      '</div>'
    ].join('\n');

    var wrapper = document.createElement('div');
    wrapper.innerHTML = html;
    while (wrapper.firstChild) document.body.appendChild(wrapper.firstChild);

    document.getElementById('cart-drawer-close').addEventListener('click', closeDrawer);
    document.getElementById('cart-drawer-overlay').addEventListener('click', closeDrawer);
  }

  /* ── inject styles ─────────────────────────────────────── */
  function injectStyles() {
    if (document.getElementById('cart-drawer-styles')) return;
    var css = [
      '#cart-drawer-overlay{position:fixed;inset:0;background:rgba(0,0,0,.45);z-index:9998;opacity:0;pointer-events:none;transition:opacity .3s}',
      '#cart-drawer-overlay.open{opacity:1;pointer-events:all}',
      '#cart-drawer{position:fixed;top:0;right:0;width:360px;max-width:95vw;height:100%;background:#fff;z-index:9999;display:flex;flex-direction:column;box-shadow:-8px 0 40px rgba(0,0,0,.18);transform:translateX(100%);transition:transform .32s cubic-bezier(.4,0,.2,1)}',
      '[data-theme="dark"] #cart-drawer{background:#1e1b18;color:#f0ede6}',
      '#cart-drawer.open{transform:translateX(0)}',
      '#cart-drawer-header{display:flex;align-items:center;justify-content:space-between;padding:1.1rem 1.25rem;border-bottom:1px solid rgba(0,0,0,.08);flex-shrink:0}',
      '[data-theme="dark"] #cart-drawer-header{border-color:rgba(255,255,255,.08)}',
      '#cart-drawer-title{font-family:inherit;font-size:1rem;font-weight:700;letter-spacing:.02em}',
      '#cart-drawer-close{background:transparent;border:none;font-size:1.1rem;cursor:pointer;color:inherit;opacity:.5;padding:.25rem .4rem;border-radius:4px;transition:opacity .2s,background .2s}',
      '#cart-drawer-close:hover{opacity:1;background:rgba(0,0,0,.06)}',
      '[data-theme="dark"] #cart-drawer-close:hover{background:rgba(255,255,255,.08)}',
      '#cart-drawer-body{flex:1;overflow-y:auto;padding:1rem 1.25rem}',
      '#cart-drawer-empty{text-align:center;padding:3rem 1rem;color:#999;font-size:.9rem}',
      '#cart-drawer-list{list-style:none;margin:0;padding:0;display:flex;flex-direction:column;gap:.85rem}',
      '.cart-drawer-item{display:flex;gap:.75rem;align-items:flex-start;padding-bottom:.85rem;border-bottom:1px solid rgba(0,0,0,.07)}',
      '[data-theme="dark"] .cart-drawer-item{border-color:rgba(255,255,255,.07)}',
      '.cart-drawer-item img{width:56px;height:72px;object-fit:cover;border-radius:6px;flex-shrink:0;background:#f0ede6}',
      '.cart-item-info{flex:1;min-width:0}',
      '.cart-item-title{font-weight:600;font-size:.85rem;line-height:1.3;margin-bottom:.15rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}',
      '.cart-item-author{font-size:.75rem;color:#888;margin-bottom:.35rem}',
      '.cart-item-price{font-weight:700;color:#e8a33a;font-size:.9rem}',
      '.cart-item-controls{display:flex;align-items:center;gap:.4rem;margin-top:.4rem}',
      '.cart-qty-btn{width:26px;height:26px;border:1.5px solid rgba(0,0,0,.15);border-radius:4px;background:transparent;font-size:.9rem;font-weight:700;cursor:pointer;display:flex;align-items:center;justify-content:center;transition:border-color .2s,background .2s;color:inherit}',
      '[data-theme="dark"] .cart-qty-btn{border-color:rgba(255,255,255,.15)}',
      '.cart-qty-btn:hover{border-color:#e8a33a;background:rgba(232,163,58,.1)}',
      '.cart-qty-val{min-width:24px;text-align:center;font-size:.85rem;font-weight:600}',
      '.cart-item-remove{background:transparent;border:none;font-size:.8rem;color:#bbb;cursor:pointer;margin-left:auto;padding:.1rem .3rem;border-radius:4px;transition:color .2s,background .2s}',
      '.cart-item-remove:hover{color:#c0392b;background:rgba(192,57,43,.08)}',
      '#cart-drawer-footer{flex-shrink:0;padding:1rem 1.25rem;border-top:1px solid rgba(0,0,0,.08)}',
      '[data-theme="dark"] #cart-drawer-footer{border-color:rgba(255,255,255,.08)}',
      '#cart-drawer-subtotal{display:flex;justify-content:space-between;align-items:center;margin-bottom:.85rem;font-size:.9rem}',
      '#cart-drawer-total{font-weight:700;font-size:1.05rem;color:#e8a33a}',
      '#cart-drawer-cta{display:block;background:#e8a33a;color:#fff;text-align:center;padding:.75rem;border-radius:8px;font-weight:700;font-size:.82rem;letter-spacing:.06em;text-transform:uppercase;text-decoration:none;transition:background .2s,transform .2s;box-shadow:0 4px 14px rgba(232,163,58,.3)}',
      '#cart-drawer-cta:hover{background:#d48f25;transform:translateY(-1px);color:#fff}',
      '.cart-badge{position:absolute;top:-6px;right:-6px;background:#e8a33a;color:#fff;border-radius:50%;width:18px;height:18px;font-size:.62rem;font-weight:700;display:flex;align-items:center;justify-content:center;line-height:1}',
      '.cart-nav-wrap{position:relative;display:inline-block}'
    ].join('\n');

    var style = document.createElement('style');
    style.id = 'cart-drawer-styles';
    style.textContent = css;
    document.head.appendChild(style);
  }

  /* ── open / close ──────────────────────────────────────── */
  function openDrawer() {
    renderDrawer();
    document.getElementById('cart-drawer').classList.add('open');
    document.getElementById('cart-drawer-overlay').classList.add('open');
    document.body.style.overflow = 'hidden';
  }

  function closeDrawer() {
    document.getElementById('cart-drawer').classList.remove('open');
    document.getElementById('cart-drawer-overlay').classList.remove('open');
    document.body.style.overflow = '';
  }

  /* ── render items ──────────────────────────────────────── */
  function renderDrawer() {
    var items = isLoggedIn() ? null : guestCart(); // server-side handled separately
    if (!isLoggedIn()) {
      renderItems(items);
      return;
    }
    // logged-in: fetch from API
    window.BookstoreCart.getCart().then(function (data) {
      var serverItems = (data && data.items) ? data.items : [];
      renderItems(serverItems);
    }).catch(function () {
      renderItems([]);
    });
  }

  function renderItems(items) {
    var list  = document.getElementById('cart-drawer-list');
    var empty = document.getElementById('cart-drawer-empty');
    var total = document.getElementById('cart-drawer-total');

    list.innerHTML = '';

    if (!items || items.length === 0) {
      empty.style.display = 'block';
      list.style.display = 'none';
      total.textContent = '$0.00';
      return;
    }

    empty.style.display = 'none';
    list.style.display = 'flex';

    var subtotal = 0;
    items.forEach(function (item) {
      var price = Number(item.price) || 0;
      var qty   = Number(item.quantity) || 1;
      subtotal += price * qty;

      var li = document.createElement('li');
      li.className = 'cart-drawer-item';
      li.dataset.bookId = item.bookId;
      li.innerHTML = [
        '<img src="' + (item.imageUrl || 'images/product-placeholder.jpg') + '" alt="' + escAttr(item.title) + '" onerror="this.src=\'images/product-placeholder.jpg\'">',
        '<div class="cart-item-info">',
        '  <div class="cart-item-title">' + escHtml(item.title) + '</div>',
        '  <div class="cart-item-author">' + escHtml(item.author) + '</div>',
        '  <div class="cart-item-price">$' + (price * qty).toFixed(2) + '</div>',
        '  <div class="cart-item-controls">',
        '    <button class="cart-qty-btn" data-action="dec" aria-label="Decrease">−</button>',
        '    <span class="cart-qty-val">' + qty + '</span>',
        '    <button class="cart-qty-btn" data-action="inc" aria-label="Increase">+</button>',
        '    <button class="cart-item-remove" aria-label="Remove">✕ Remove</button>',
        '  </div>',
        '</div>'
      ].join('');

      /* quantity controls */
      li.querySelector('[data-action="dec"]').addEventListener('click', function () {
        if (qty <= 1) { removeItem(item.bookId); return; }
        changeQty(item.bookId, qty - 1);
      });
      li.querySelector('[data-action="inc"]').addEventListener('click', function () {
        changeQty(item.bookId, qty + 1);
      });
      li.querySelector('.cart-item-remove').addEventListener('click', function () {
        removeItem(item.bookId);
      });

      list.appendChild(li);
    });

    total.textContent = '$' + subtotal.toFixed(2);
    updateBadge(items.reduce(function (s, i) { return s + (i.quantity || 1); }, 0));
  }

  function changeQty(bookId, newQty) {
    if (isLoggedIn()) {
      window.BookstoreCart.updateCartItem(bookId, newQty).then(function () {
        renderDrawer();
        updateNavBadgeFromAPI();
      }).catch(function (e) { console.error(e); });
    } else {
      guestUpdateQty(bookId, newQty);
      renderDrawer();
      updateBadgeFromGuest();
    }
  }

  function removeItem(bookId) {
    if (isLoggedIn()) {
      window.BookstoreCart.removeCartItem(bookId).then(function () {
        renderDrawer();
        updateNavBadgeFromAPI();
      }).catch(function (e) { console.error(e); });
    } else {
      guestRemove(bookId);
      renderDrawer();
      updateBadgeFromGuest();
    }
  }

  /* ── navbar cart icon → opens drawer ──────────────────── */
  function wireNavCartLink() {
    var cartLink = document.querySelector('.right-element .cart');
    if (!cartLink) return;

    /* wrap in relative div for badge positioning */
    var wrap = document.createElement('span');
    wrap.className = 'cart-nav-wrap';
    cartLink.parentNode.insertBefore(wrap, cartLink);
    wrap.appendChild(cartLink);

    /* badge element */
    var badge = document.createElement('span');
    badge.className = 'cart-badge';
    badge.id = 'cart-nav-badge';
    badge.style.display = 'none';
    wrap.appendChild(badge);

    cartLink.addEventListener('click', function (e) {
      e.preventDefault();
      openDrawer();
    });

    /* update initial badge */
    if (isLoggedIn()) {
      updateNavBadgeFromAPI();
    } else {
      updateBadgeFromGuest();
    }
  }

  function updateBadge(count) {
    var badge = document.getElementById('cart-nav-badge');
    if (!badge) return;
    if (count > 0) {
      badge.textContent = count > 99 ? '99+' : count;
      badge.style.display = 'flex';
    } else {
      badge.style.display = 'none';
    }
  }

  function updateBadgeFromGuest() {
    var items = guestCart();
    var count = items.reduce(function (s, i) { return s + (i.quantity || 1); }, 0);
    updateBadge(count);
  }

  function updateNavBadgeFromAPI() {
    if (!isLoggedIn() || !window.BookstoreCart) return;
    window.BookstoreCart.getCart().then(function (data) {
      var qty = (data && data.totalQuantity) ? data.totalQuantity : 0;
      updateBadge(qty);
    }).catch(function () {});
  }

  /* ── wire all add-to-cart buttons ──────────────────────── */
  function wireAddToCartButtons() {
    var btns = document.querySelectorAll('.add-to-cart[data-book-id]');
    btns.forEach(function (btn) {
      /* prevent double-binding */
      if (btn.dataset.drawerBound) return;
      btn.dataset.drawerBound = '1';

      btn.addEventListener('click', async function () {
        var book = {
          bookId:   btn.dataset.bookId,
          title:    btn.dataset.title,
          author:   btn.dataset.author,
          price:    Number(btn.dataset.price),
          imageUrl: btn.dataset.imageUrl,
          quantity: 1
        };

        // Require login before doing anything with the cart
        if (!isLoggedIn()) {
          var currentPage = window.location.pathname.split('/').pop() || 'index.html';
          window.location.href = 'login.html?redirect=' + encodeURIComponent(currentPage);
          return;
        }

        var orig = btn.textContent;
        btn.disabled = true;
        btn.textContent = '✓ Added';

        try { await window.BookstoreCart.addToCart(book); } catch (e) { console.error(e); }
        updateNavBadgeFromAPI();
        openDrawer();

        setTimeout(function () {
          btn.textContent = orig;
          btn.disabled = false;
        }, 900);
      });
    });
  }

  /* ── helpers ───────────────────────────────────────────── */
  function escHtml(s) {
    return String(s || '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
  }
  function escAttr(s) {
    return String(s || '').replace(/"/g,'&quot;').replace(/'/g,'&#39;');
  }

  /* ── expose global hook for book-loader dynamic cards ──── */
  window.CartDrawer = { open: openDrawer, close: closeDrawer, wire: wireAddToCartButtons };

  /* ── init ──────────────────────────────────────────────── */
  document.addEventListener('DOMContentLoaded', function () {
    injectStyles();
    injectDrawer();
    wireNavCartLink();
    wireAddToCartButtons();

    /* re-wire after dynamic book-loader renders */
    var observer = new MutationObserver(function () { wireAddToCartButtons(); });
    var lists = document.querySelectorAll('.product-list, [data-tab-content]');
    lists.forEach(function (el) {
      observer.observe(el, { childList: true, subtree: true });
    });
  });
})();