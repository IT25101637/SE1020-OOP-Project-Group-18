document.addEventListener('DOMContentLoaded', function () {
    const userEmail = new URLSearchParams(window.location.search).get('email');
    if (!userEmail) {
        alert('No user email provided. Redirecting to login.');
        window.location.href = 'login.html';
        return;
    }

    fetchUserProfile(userEmail);

    document.getElementById('editProfileBtn').addEventListener('click', () => {
        window.location.href = `edit-profile.html?email=${userEmail}`;
    });

    document.getElementById('deleteAccountBtn').addEventListener('click', () => {
        if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
            deleteAccount(userEmail);
        }
    });

    document.getElementById('logoutBtn').addEventListener('click', () => {
        // Clear user session/token if any
        window.location.href = 'login.html';
    });
});

function fetchUserProfile(email) {
    console.log('Fetching profile for email:', email);
    
    // First, try the path-based endpoint
    fetch(`/api/users/profile/${email}`)
        .then(response => {
            console.log('Response status:', response.status);
            return response.json().then(data => ({
                status: response.status,
                data: data
            }));
        })
        .then(({status, data}) => {
            console.log('Response data:', data);
            
            // Check if we got a success response
            if (status === 200 && data && (data.status === 'success' || data.user)) {
                // Handle both response formats
                const userData = data.user || data;
                populateProfile(userData);
                return;
            }
            
            // If path-based endpoint failed, try query parameter endpoint
            return fetch(`/api/users/profile?email=${encodeURIComponent(email)}`)
                .then(response => response.json())
                .then(data => {
                    console.log('Fallback response data:', data);
                    if (data && (data.status === 'success' || data.user)) {
                        const userData = data.user || data;
                        populateProfile(userData);
                    } else {
                        console.error('Failed to load profile:', data);
                        hideLoadingShowProfile();
                    }
                });
        })
        .catch(error => {
            console.error('Error fetching profile:', error);
            hideLoadingShowProfile();
        });
}

function hideLoadingShowProfile() {
    document.getElementById('loadingText').style.display = 'none';
    document.getElementById('profileDisplay').style.display = 'block';
    document.getElementById('errorMessage').style.display = 'none';
}


function populateProfile(user) {
    try {
        document.getElementById('displayUsername').textContent = user.username || '-';
        document.getElementById('displayEmail').textContent = user.email || '-';
        document.getElementById('displayFirstName').textContent = user.firstName || '-';
        document.getElementById('displayLastName').textContent = user.lastName || '-';
        document.getElementById('displayPhoneNumber').textContent = user.phoneNumber || '-';
        document.getElementById('displayPoints').textContent = user.points || '0';

        document.getElementById('loadingText').style.display = 'none';
        document.getElementById('profileDisplay').style.display = 'block';
        document.getElementById('errorMessage').style.display = 'none';
    } catch (error) {
        console.error('Error populating profile:', error);
        hideLoadingShowProfile();
    }
}

function deleteAccount(email) {
    fetch(`/api/users/delete/${email}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || `HTTP Error: ${response.status}`);
            });
        }
        return response.json();
    })
    .then(data => {
        if (data && data.status === 'success') {
            alert('Account deleted successfully.');
            window.location.href = 'register.html';
        } else {
            alert('Failed to delete account: ' + (data?.message || 'Unknown error'));
        }
    })
    .catch(error => {
        console.error('Error deleting account:', error);
        alert('An error occurred while deleting your account: ' + error.message);
    });
}
