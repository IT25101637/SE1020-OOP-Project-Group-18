// Dynamic navbar authentication/cart state handler
document.addEventListener('DOMContentLoaded', function() {
    updateNavbarAuthLink();
    updateNavbarCartSummary();
});

function updateNavbarAuthLink() {
    const currentUserEmail = sessionStorage.getItem('currentUserEmail');
    const lastMenuItem = document.querySelector('.menu-list .menu-item:last-child');
    const accountLink = document.querySelector('.right-element .user-account');

    if (lastMenuItem) {
        if (currentUserEmail) {
            lastMenuItem.innerHTML = `<a href="profile.html?email=${encodeURIComponent(currentUserEmail)}" class="nav-link">Profile</a>`;
        } else {
            lastMenuItem.innerHTML = '<a href="login.html" class="nav-link">Login</a>';
        }
    }

    if (accountLink) {
        if (currentUserEmail) {
            accountLink.setAttribute('href', `profile.html?email=${encodeURIComponent(currentUserEmail)}`);
            accountLink.innerHTML = '<i class="icon icon-user"></i><span>My Account</span>';
        } else {
            accountLink.setAttribute('href', 'login.html');
            accountLink.innerHTML = '<i class="icon icon-user"></i><span>Account</span>';
        }
    }
}

async function updateNavbarCartSummary() {
    const cartLink = document.querySelector('.right-element .cart');
    if (!cartLink) {
        return;
    }

    const currentUserId = sessionStorage.getItem('currentUserId');
    cartLink.setAttribute('href', 'cart.html');

    if (!currentUserId) {
        cartLink.innerHTML = '<i class="icon icon-clipboard"></i><span>Cart:(0 $0.00)</span>';
        return;
    }

    try {
        const response = await fetch(`/api/cart/${encodeURIComponent(currentUserId)}`);
        const data = await response.json();

        if (response.ok && data.status === 'success') {
            const qty = data.totalQuantity || 0;
            const subtotal = Number(data.subtotal || 0).toFixed(2);
            cartLink.innerHTML = `<i class="icon icon-clipboard"></i><span>Cart:(${qty} $${subtotal})</span>`;
            return;
        }
    } catch (error) {
        console.error('Error fetching cart summary:', error);
    }

    cartLink.innerHTML = '<i class="icon icon-clipboard"></i><span>Cart:(0 $0.00)</span>';
}

window.updateNavbarCartSummary = updateNavbarCartSummary;
