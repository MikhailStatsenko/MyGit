var firstLoad = true
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('commit-changes-btn') .addEventListener("click", function() {
        fetchStatusData();
        if (firstLoad) {
            setTimeout(() => showElement('commit-page-content'), 1000);
            firstLoad = false;
        } else {
            setTimeout(() => showElement('commit-page-content'), 200);
        }
    });
})

function fetchStatusData() {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/status/${userId_}/${repositoryName_}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const untrackedList = document.getElementById('unindexed-files-list');
            const modifiedList = document.getElementById('indexed-files-list');
            const untrackedFilesHeader = document.getElementById('untracked-files-header');
            const nothingToCommit = document.getElementById('no-files-in-index-msg');
            const commitBtn = document.getElementById('commit-btn');

            untrackedList.innerHTML = '';
            modifiedList.innerHTML = '';
            setCurrentBranch();

            const backFromCommit = document.createElement('i');
            backFromCommit.classList.add('fas', 'fa-arrow-left', 'repository-nav-button');
            backFromCommit.addEventListener('click', () => {
                backFromCommit.remove();
                showElement('repository-page-content');
            });

            if (data.unindexed.length > 0) {
                untrackedList.classList.remove('hidden');
                untrackedFilesHeader.innerText = 'Неотслеживаемые файлы';

                data.unindexed.sort();
                data.unindexed.forEach(file => {
                    const addToIndex = document.createElement('i');
                    addToIndex.classList.add('fas', 'fa-plus');
                    addToIndex.style.cursor = 'pointer';
                    addToIndex.addEventListener('click', () => addFileToIndex(file));

                    const listItem = document.createElement('li');
                    listItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
                    listItem.textContent = file;
                    listItem.appendChild(addToIndex);
                    untrackedList.appendChild(listItem);
                });

                const wrapper = document.getElementById('untracked-files-header-wrapper')
                wrapper.appendChild(backFromCommit);
            } else {
                const wrapper = document.getElementById('indexed-files-header-wrapper')
                wrapper.appendChild(backFromCommit);
                untrackedList.classList.add('hidden');
                untrackedFilesHeader.innerText = '';
            }

            if (data.indexed.length > 0) {
                commitBtn.classList.remove('disabled');
                modifiedList.classList.remove('hidden');
                nothingToCommit.classList.add('hidden');

                data.indexed.sort();
                data.indexed.forEach(file => {
                    const removeFromIndex = document.createElement('i');
                    removeFromIndex.classList.add('fas', 'fa-minus');
                    removeFromIndex.style.cursor = 'pointer';
                    removeFromIndex.addEventListener('click', () => removeFileFromIndex(file));

                    const listItem = document.createElement('li');
                    listItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
                    listItem.textContent = file;
                    listItem.appendChild(removeFromIndex);
                    modifiedList.appendChild(listItem);
                });
            } else {
                commitBtn.classList.add('disabled');
                modifiedList.classList.add('hidden');
                nothingToCommit.classList.remove('hidden')
            }
        })
        .catch(error => console.error('Error fetching status data:', error));
}

function commitChanges() {
    const commitMessage = document.getElementById('commit-message').value;

    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/commit/${userId_}/${repositoryName_}` + (commitMessage !== '' ? "?message=" + commitMessage : ''), {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                fetchStatusData();
            } else {
                console.error('Error committing changes:', response.status);
                console.error(response.text());
            }
        })
        .catch(error => console.error('Error committing changes:', error));
}

function addFileToIndex(file) {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/add/${userId_}/${repositoryName_}?pattern=${file}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    }).then(response => response.json())
        .then(data => {
            fetchStatusData();
        })
        .catch(error => console.error('Ошибка при добавлении файла в индекс:', error));
}

function removeFileFromIndex(file) {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/remove/${userId_}/${repositoryName_}?pattern=${file}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    }).then(response => response.json())
        .then(data => {
            fetchStatusData();
        })
        .catch(error => console.error('Ошибка при удалении файла из индекса:', error));
}

function setCurrentBranch() {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/branch/list/${userId_}/${repositoryName_}`, {
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
            const currentBranch = document.getElementById('current-branch-to-commit');
            currentBranch.innerText = `Текущая ветка: ${data.currentBranch}`;
        })
        .catch(error => {
            console.error('There has been a problem with your fetch operation:', error);
        });
}