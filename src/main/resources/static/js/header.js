document.addEventListener('DOMContentLoaded', function() {
    const jwtToken = localStorage.getItem('jwtToken');

    if (jwtToken && !isTokenExpired(jwtToken)) {
        try {
            const tokenData = JSON.parse(atob(jwtToken.split('.')[1]));
            const username = document.getElementById('username-header');
            const headerLinks = document.getElementById('auth-header-links');
            const accountInfo = document.getElementById('account-info-header');

            const sub = tokenData.sub;

            headerLinks.style.display = 'none';

            username.textContent = sub;

            const logoutLink = document.createElement('a');
            logoutLink.textContent = '(Выйти)';
            logoutLink.href = '#';
            logoutLink.onclick = function() {
                localStorage.removeItem('jwtToken');
                location.reload();
            };
            accountInfo.appendChild(logoutLink);
            accountInfo.style.display = 'flex';
        } catch (error) {
            console.error('Ошибка при обработке токена:', error);
        }
    }
});

function isTokenExpired(token) {
    if (token) {
        try {
            const tokenData = JSON.parse(atob(token.split('.')[1]));
            const currentTime = Math.floor(Date.now() / 1000); // Текущее время в секундах

            return currentTime > tokenData.exp;
        } catch (error) {
            console.error('Ошибка при обработке токена:', error);
            return true;
        }
    }
    return true;
}