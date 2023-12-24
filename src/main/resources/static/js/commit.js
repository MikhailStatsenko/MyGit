document.getElementById('commit-changes-btn') .addEventListener("click", function() {
    showElement('commit-page-content')
    fetchStatusData();
});

// const listItem = document.createElement('li');
// listItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
//
// const branchName = document.createElement('span');
// branchName.textContent = branch;
// branchName.style.flex = '1';
//
// if (data.currentBranch !== branch) {
//     branchName.style.cursor = 'pointer';
//     branchName.addEventListener('click', () => {switchBranch(branch);});
// }
//
// const activeBranchIcon = document.createElement('i');
// activeBranchIcon.classList.add('fas', 'fa-check');
// if (data.currentBranch !== branch) {
//     activeBranchIcon.classList.add('text-white')
// }
// listItem.appendChild(activeBranchIcon);
//
// const deleteIcon = document.createElement('i');
// deleteIcon.classList.add('fas', 'fa-trash', 'ml-2');
// if (data.currentBranch !== branch) {
//     deleteIcon.style.cursor = 'pointer';
//     deleteIcon.addEventListener('click', () => deleteBranch(branch));
// } else {
//     deleteIcon.classList.add('text-muted');
// }
//
// listItem.appendChild(branchName);
// listItem.appendChild(deleteIcon);
// branchesList.appendChild(listItem);

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
            const untrackedList = document.getElementById('untracked-files-list');
            const modifiedList = document.getElementById('modified-files-list');
            const untrackedFilesHeader = document.getElementById('untracked-files-header');

            untrackedList.innerHTML = '';
            modifiedList.innerHTML = '';

            if (data.untracked.length > 0) {
                untrackedFilesHeader.innerText = 'Untracked files';
                data.untracked.forEach(file => {
                    const listItem = document.createElement('li');
                    listItem.textContent = file;
                    untrackedList.appendChild(listItem);
                });
            } else {
                untrackedFilesHeader.innerText = '';
            }

            if (data.modified.length > 0) {
                data.modified.forEach(file => {
                    const listItem = document.createElement('li');
                    listItem.textContent = file;
                    modifiedList.appendChild(listItem);
                });
            } else {
                const nothingToCommit = document.createElement('p');
                nothingToCommit.textContent = "Nothing to commit";
                modifiedList.appendChild(nothingToCommit);
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
            }
        })
        .catch(error => console.error('Error committing changes:', error));
}
