document.getElementById('registerFormElement').addEventListener('submit', function (event) {
    event.preventDefault();

    const registerUsername = document.getElementById('registerUsername').value;
    const registerEmail = document.getElementById('registerEmail').value;
    const registerPassword = document.getElementById('registerPassword').value;

    fetch('/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: registerUsername,
            email: registerEmail,
            password: registerPassword
        })
    })
        .then(async response => {
            if (!response.ok) {
                throw new Error(await response.text());
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
        });
});