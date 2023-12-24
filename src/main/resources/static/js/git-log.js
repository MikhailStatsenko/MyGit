document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('git-log-btn').addEventListener('click', function() {
        getBranches();
        fetchCommitInfo();
    });
});

document.getElementById('back-from-git-log').addEventListener('click', function () {
    showElement('repository-page-content')
});

function fillBranchSelector(branches) {
    const branchSelector = document.getElementById('branch-selector');
    branchSelector.innerHTML = '';

    branches.forEach(branch => {
        const option = document.createElement('option');
        option.value = branch;
        option.textContent = branch;
        branchSelector.appendChild(option);
    });
    branchSelector.value = 'master';

    branchSelector.addEventListener('change', function() {
        const selectedBranch = this.value;
        fetchCommitInfo(selectedBranch);
    });
}

function getBranches() {
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
            fillBranchSelector(data.branches, data.currentBranch);
            fetchCommitInfo('master');
        })
        .catch(error => {
            console.error('There has been a problem with your fetch operation:', error);
        });
}


function fetchCommitInfo(branch = '') {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/git/log/${userId_}/${repositoryName_}` + (branch === '' ? '?branch=master' : '?branch=' + branch), {
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
                commitItem.classList.add('commit-item');

                const commitMessage = document.createElement('h6');
                commitMessage.textContent = commit.message.length > 50 ? `${commit.message.slice(0, 60)}...` : commit.message;

                const commitDetails = document.createElement('div');
                commitDetails.textContent = `${commit.date}, ${commit.hash.slice(0, 8)}`;

                commitItem.appendChild(commitMessage);
                commitItem.appendChild(commitDetails);

                commitList.appendChild(commitItem);
            });

            showElement('commit-info');
        })
        .catch(error => {
            console.error('There has been a problem with your fetch operation:', error);
        });
}