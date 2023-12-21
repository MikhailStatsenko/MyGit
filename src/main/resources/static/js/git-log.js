document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('git-log-btn').addEventListener('click', function() {
        fetchCommitInfo();
    });
});

document.getElementById('back-from-git-log').addEventListener('click', function () {
    showElement('repository-page-content')
});


function fetchCommitInfo() {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/log/${userId_}/${repositoryName_}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const commitList = document.getElementById('commit-list');
            commitList.innerHTML = '';

            data.forEach(commit => {
                const commitItem = document.createElement('li');
                commitItem.classList.add('commit-item'); // Добавляем класс для стилизации элемента

                // Создаем заголовок h6 с обрезанным сообщением коммита
                const commitMessage = document.createElement('h6');
                commitMessage.textContent = commit.message.length > 50 ? `${commit.message.slice(0, 60)}...` : commit.message;

                // Создаем контейнер для даты и хэша коммита
                const commitDetails = document.createElement('div');
                commitDetails.textContent = `${commit.date}, ${commit.hash.slice(0, 8)}`;

                // Добавляем созданные элементы в элемент списка
                commitItem.appendChild(commitMessage);
                commitItem.appendChild(commitDetails);

                commitList.appendChild(commitItem);
            });

            showElement('commit-info'); // Показываем блок с информацией о коммитах
        })
        .catch(error => {
            console.error('There has been a problem with your fetch operation:', error);
        });
}