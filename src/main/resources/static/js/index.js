function addUserToList(userList) {
    const ul = document.getElementById('userList');
    userList.forEach(user => {
        const li = document.createElement('li');
        const userLink = document.createElement('a');
        userLink.href = `/api/file/${user}`;

        userLink.addEventListener('click', function(event) {
            event.preventDefault();
            const jwtToken = localStorage.getItem('jwtToken');
            if (jwtToken && !isTokenExpired(jwtToken)) {
                try {
                    const tokenData = JSON.parse(atob(jwtToken.split('.')[1]));
                    const message = user === tokenData.sub ? "Ваши репозитории" : `Репозитории ${user}`;
                    fetchUserRepositories(user, jwtToken, message);
                } catch (error) {
                    console.error('Ошибка при обработке токена:', error);
                }
            }
        });

        userLink.textContent = user;
        li.appendChild(userLink);
        ul.appendChild(li);
    });
}

function handleContentDisplayChange() {
    if (document.getElementById('home-page-content').style.display !== 'none') {
        fetch('/api/user/all')
            .then(response => response.json())
            .then(data => addUserToList(data.users))
            .catch(error => console.error('Ошибка:', error));
    }
}

document.addEventListener('DOMContentLoaded', handleContentDisplayChange);



document.addEventListener('DOMContentLoaded', function() {
    const jwtToken = localStorage.getItem('jwtToken');

    if (jwtToken && !isTokenExpired(jwtToken)) {
        try {
            const tokenData = JSON.parse(atob(jwtToken.split('.')[1]));
            const userId = tokenData.sub;

            fetchUserRepositories(userId, jwtToken, "Ваши репозитории");
        } catch (error) {
            console.error('Ошибка при обработке токена:', error);
        }
    }
});


function fetchUserRepositories(userId, jwtToken, message) {
    const title = document.getElementById('repos-title');
    const repositoriesElement = document.getElementById('my-repositories');
    const repositoriesListElement = document.getElementById('my-repositories-list');
    const repositoriesElementUnauthorized = document.getElementById('my-repositories-unauthorized');

    fetch(`/api/file/${userId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
        .then(response => {
            if (!response.ok) {
                title.innerHTML = message;
                repositoriesListElement.textContent = 'Репозитории отсутствуют'
                repositoriesElementUnauthorized.style.display = 'none';
                repositoriesElement.style.display = 'block';
            }
            return response.json();
        })
        .then(data => {
            const repositoriesList = data.repositories;

            repositoriesListElement.innerHTML = '';
            repositoriesList.forEach(repository => {
                const listItem = document.createElement('li');
                listItem.textContent = repository;
                repositoriesListElement.appendChild(listItem);
            });
            title.innerHTML = message;

            repositoriesElementUnauthorized.style.display = 'none';
            repositoriesElement.style.display = 'block';
        })
}
