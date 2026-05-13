document.addEventListener('DOMContentLoaded', function () {
    const userEmail = new URLSearchParams(window.location.search).get('email');
    if (!userEmail) {
        alert('No user email provided. Redirecting to login.');
        window.location.href = 'login.html';
        return;
    }

    // Fetch and populate existing user data
    fetch(`/api/users/profile/${userEmail}`)
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                document.getElementById('firstName').value = data.user.firstName;
                document.getElementById('lastName').value = data.user.lastName;
                document.getElementById('phoneNumber').value = data.user.phoneNumber;
            } else {
                alert('Could not load profile data.');
            }
        });

    const editProfileForm = document.getElementById('editProfileForm');
    editProfileForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const updatedData = {
            email: userEmail,
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            phoneNumber: document.getElementById('phoneNumber').value
        };

        fetch('/api/users/update', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                alert('Profile updated successfully!');
                                window.location.href = `profile.html?email=${userEmail}`;
            } else {
                alert('Update failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error updating profile:', error);
            alert('An error occurred while updating your profile.');
        });
    });
});
