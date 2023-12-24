var path;
var userId_;
var repositoryName_;
function showRepositoryContents(userId, repositoryName) {
    const repositoryTitle = document.getElementById('repository-title');
    repositoryTitle.textContent = repositoryName;
    path = userId + '/' + repositoryName;
    userId_ = userId;
    repositoryName_ = repositoryName;
    fetchRepositoryContent();
}

const upTheHierarchyElement = document.getElementById('up-the-hierarchy');
const backFromRepository = document.getElementById('back-from-repository');
const deleteDirOrFile = document.getElementById('delete-dir-or-file');
function fetchRepositoryContent() {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/file/${path}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const repositoryTable = document.getElementById('repository-content-table');
            const repositoryContent = document.getElementById('repository-content');
            repositoryContent.innerHTML = '';

            const createDirectory = document.getElementById('create-directory');
            const uploadFiles = document.getElementById('upload-files-to-repository');
            uploadFiles.classList.remove('hidden');
            createDirectory.classList.remove('hidden');

            for (const [itemName, itemType] of Object.entries(data)) {
                const row = document.createElement('tr');
                const cell = document.createElement('td');
                const itemLink = document.createElement('a');
                itemLink.textContent = itemName;
                itemLink.href = '#';
                if (itemType === 'directory') {
                    itemLink.addEventListener('click', function (event) {
                        event.preventDefault();
                        path += '/' + itemName;
                        fetchRepositoryContent();
                    });
                } else {
                    itemLink.addEventListener('click', function (event) {
                        event.preventDefault();
                        uploadFiles.classList.add('hidden');
                        createDirectory.classList.add('hidden');
                        path += '/' + itemName;
                        readFile();
                    });
                }

                const icon = document.createElement('i');
                if (itemType === 'file') {
                    icon.classList.add('far', 'fa-file');
                } else {
                    icon.classList.add('far', 'fa-folder');
                }
                icon.classList.add('icon');
                itemLink.prepend(icon);

                cell.appendChild(itemLink);
                row.appendChild(cell);
                repositoryContent.appendChild(row);
            }
            if (repositoryContent.innerHTML === '') {
                repositoryTable.classList.remove('table-hover');
                const row = document.createElement('tr');
                const cell = document.createElement('td');
                cell.innerHTML = 'Репозиторий пока что пустой'
                row.appendChild(cell);
                repositoryContent.appendChild(row);
            } else {
                repositoryTable.classList.add('table-hover');
            }

            if (path.split("/").length > 2) {
                upTheHierarchyElement.classList.remove("hidden")
                deleteDirOrFile.classList.remove("hidden")
            } else {
                upTheHierarchyElement.classList.add("hidden")
                deleteDirOrFile.classList.add("hidden")
            }
        })
        .catch(error => console.error('Ошибка при загрузке содержимого репозитория:', error));
}

upTheHierarchyElement.addEventListener('click', function () {removeLastPathPart()})
backFromRepository.addEventListener('click', function () {showElement('home-page-content');})

function removeLastPathPart() {
    var lastSlashIndex = path.lastIndexOf('/');
    path = path.substring(0, lastSlashIndex);
    fetchRepositoryContent();
}

function readFile() {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/file/${path}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.text())
        .then(data => {
            const repositoryContent = document.getElementById('repository-content');
            repositoryContent.innerHTML = '';
            const row = document.createElement('tr');
            row.classList.add("overflow-scroll")
            
            const cell = document.createElement('td');
            const text = document.createElement('pre')

            if (data !== '') {
                text.textContent = data;
            } else {
                text.classList.add("text-secondary");
                text.textContent = "Файл не содержит данных"
            }
            text.classList.add("p-2");
            text.classList.add("m-0");

            cell.classList.add("text-document");
            cell.classList.add("m-0");
            cell.classList.add("p-0");

            cell.appendChild(text);
            row.appendChild(cell);
            repositoryContent.appendChild(row);


            if (path.split("/").length > 2) {
                upTheHierarchyElement.classList.remove("hidden")
                deleteDirOrFile.classList.remove("hidden")
            } else {
                upTheHierarchyElement.classList.add("hidden")
                deleteDirOrFile.classList.add("hidden")
            }
        })
        .catch(error => console.error('Ошибка при загрузке содержимого репозитория:', error));
}


const createDirectoryIcon = document.getElementById('create-directory');
createDirectoryIcon.addEventListener('click', function () {
    const repositoryContent = document.getElementById('repository-content');
    const newRow = document.createElement('tr');

    const newCell = document.createElement('td');

    const icon = document.createElement('i');
    icon.classList.add('far', 'fa-folder', 'icon');
    newCell.appendChild(icon);

    const inputField = document.createElement('input');
    inputField.setAttribute('type', 'text');
    inputField.setAttribute('required', 'true')
    inputField.setAttribute('placeholder', 'Название папки');
    inputField.classList.add('new-dir-name-input', 'form-control');
    newCell.appendChild(inputField);

    const confirmIcon = document.createElement('i');
    confirmIcon.classList.add('fas', 'fa-check');
    confirmIcon.style.cursor = 'pointer';
    confirmIcon.addEventListener('click', function () {
        const folderName = inputField.value;
        if (folderName.trim() !== '') {
            createDirectory(folderName);
        } else {
            console.error('Folder name cannot be empty');
        }
    });
    newCell.appendChild(confirmIcon);

    newRow.appendChild(newCell);
    repositoryContent.insertBefore(newRow, repositoryContent.firstChild);
    inputField.focus();
});

function createDirectory(folderName) {
    const jwtToken = localStorage.getItem('jwtToken');
    fetch(`/api/file/add-directory/${path}?name=${folderName}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${jwtToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                fetchRepositoryContent();
            } else {
                console.error('Error creating directory:', response.status);
            }
        })
        .catch(error => console.error('Error creating directory:', error));
}

document.addEventListener('click', function(event) {
    const createDirectoryIcon = document.getElementById('create-directory');
    const inputField = document.querySelector('#repository-content-table input[type="text"]');
    const confirmIcon = document.querySelector('#repository-content-table i.fas.fa-check');

    if (event.target !== createDirectoryIcon && event.target !== inputField && event.target !== confirmIcon) {
        cancelCreatingDirectory();
    }
});

function cancelCreatingDirectory() {
    const inputField = document.querySelector('#repository-content-table input[type="text"]');
    if (inputField) {
        const row = inputField.closest('tr');
        row.remove();
    }
}