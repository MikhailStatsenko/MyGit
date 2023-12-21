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
                    if (user === tokenData.sub) {
                        fetchUserRepositories(user, jwtToken, "Ваши репозитории", true);
                    } else {
                        fetchUserRepositories(user, jwtToken, `Репозитории ${user}`, false);
                    }
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

            fetchUserRepositories(userId, jwtToken, "Ваши репозитории", true);
        } catch (error) {
            console.error('Ошибка при обработке токена:', error);
        }
    }
});


function fetchUserRepositories(userId, jwtToken, message, isAbleToCreateRepo) {
    const title = document.getElementById('repos-title');
    const repositoriesElement = document.getElementById('my-repositories');
    const repositoriesListElement = document.getElementById('my-repositories-list');
    const repositoriesElementUnauthorized = document.getElementById('my-repositories-unauthorized');
    const addNewRepo = document.getElementById('add-new-repository');


    fetch(`/api/file/${userId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
        .then(response => {
            if (!response.ok) {
                repositoriesListElement.textContent = 'Репозитории отсутствуют'
            }
            return response.json();
        })
        .then(data => {
            const repositoriesList = data.repositories;

            repositoriesListElement.innerHTML = '';
            repositoriesList.forEach(repository => {
                const listItem = document.createElement('li');
                const repositoryLink = document.createElement('a');

                repositoryLink.href = `/api/file/${userId}/${repository}`;
                repositoryLink.textContent = repository;

                repositoryLink.addEventListener('click', function (event) {
                    event.preventDefault();
                    showRepositoryContents(userId, repository);
                    showElement('repository-page-content');
                })

                listItem.appendChild(repositoryLink);
                repositoriesListElement.appendChild(listItem);
            });
        })
    const errorMessage = document.getElementById('add-repository-error-message');

    title.innerHTML = message;
    addNewRepo.style.display = isAbleToCreateRepo ? 'flex' : 'none';
    errorMessage.style.display = isAbleToCreateRepo ? 'block' : 'none';
    repositoriesElementUnauthorized.style.display = 'none';
    repositoriesElement.style.display = 'block';
}



function addRepository(event) {
    event.preventDefault();
    const repositoryNameElement = document.getElementById('new-repository-name');
    const repositoryName = repositoryNameElement.value;

    const pattern = /[a-zA-Z0-9_\-]+/;
    if (!pattern.test(repositoryName)) {
        repositoryNameElement.value = '';

        const errorMessage = document.getElementById('add-repository-error-message');
        errorMessage.style.opacity = '1';
        setTimeout(() => {
            errorMessage.style.opacity = '0';
        }, 3000);
        return;
    }

    const jwtToken = localStorage.getItem('jwtToken');
    const tokenData = JSON.parse(atob(jwtToken.split('.')[1]));
    const userId = tokenData.sub;

    fetch(`/api/git/init/${userId}/${repositoryName}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            repositoryNameElement.value = "";
            fetchUserRepositories(userId, jwtToken, "Ваши репозитории", true);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}
