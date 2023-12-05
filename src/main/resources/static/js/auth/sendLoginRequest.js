document.getElementById('loginFormElement').addEventListener('submit', function (event) {
    event.preventDefault();

    const loginUsername = document.getElementById('loginUsername').value;
    const loginPassword = document.getElementById('loginPassword').value;
    const errorMessage = document.getElementById('login-error-message');

    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: loginUsername,
            password: loginPassword
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка входа');
            }
            return response.json();
        })
        .then(data => {
            const token = data.token;
            localStorage.setItem('jwtToken', token);
            location.reload();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            errorMessage.style.opacity = '1';

            setTimeout(() => {
                errorMessage.style.opacity = '0';
            }, 3000);
        });
});