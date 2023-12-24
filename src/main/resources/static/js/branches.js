function fetchBranches() {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/branch/list/${userId_}/${repositoryName_}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const branchesList = document.getElementById('branches-list');
            branchesList.innerHTML = '';

            data.branches.forEach(branch => {
                const listItem = document.createElement('li');
                listItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');

                const branchName = document.createElement('span');
                branchName.textContent = branch;
                branchName.style.flex = '1';

                if (data.currentBranch !== branch) {
                    branchName.style.cursor = 'pointer';
                    branchName.addEventListener('click', () => {switchBranch(branch);});
                }

                const activeBranchIcon = document.createElement('i');
                activeBranchIcon.classList.add('fas', 'fa-check');
                if (data.currentBranch !== branch) {
                    activeBranchIcon.classList.add('text-white')
                }
                listItem.appendChild(activeBranchIcon);

                const deleteIcon = document.createElement('i');
                deleteIcon.classList.add('fas', 'fa-trash', 'ml-2');
                if (data.currentBranch !== branch) {
                    deleteIcon.style.cursor = 'pointer';
                    deleteIcon.addEventListener('click', () => deleteBranch(branch));
                } else {
                    deleteIcon.classList.add('text-muted');
                }

                listItem.appendChild(branchName);
                listItem.appendChild(deleteIcon);
                branchesList.appendChild(listItem);
            });
        })
        .catch(error => console.error('Ошибка при загрузке веток:', error));
}


function addBranch() {
    const branchName = document.getElementById('new-branch-name').value;
    if (!branchName)
        return;

    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/branch/create/${userId_}/${repositoryName_}?branch=${branchName}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            fetchBranches();
        })
        .catch(error => console.error('Ошибка при добавлении ветки:', error));

    document.getElementById('new-branch-name').value = '';
}

function deleteBranch(branchName) {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/branch/delete/${userId_}/${repositoryName_}?branch=${branchName}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            fetchBranches();
        })
        .catch(error => console.error('Ошибка при удалении ветки:', error));
}

function switchBranch(branchName) {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/branch/switch/${userId_}/${repositoryName_}?branch=${branchName}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            fetchBranches(); // Перезагрузка списка веток после смены ветки
        })
        .catch(error => console.error('Ошибка при смене ветки:', error));
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('branches-btn').addEventListener('click', function() {
        showElement('branches-page-content');
        fetchBranches();
    });
});


document.getElementById('back-from-branches').addEventListener('click', function () {
    showElement('repository-page-content')
});
