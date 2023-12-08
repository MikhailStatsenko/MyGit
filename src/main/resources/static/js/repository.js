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
                        path += '/' + itemName;
                        readRile();
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
            } else {
                upTheHierarchyElement.classList.add("hidden")
            }
        })
        .catch(error => console.error('Ошибка при загрузке содержимого репозитория:', error));
}

upTheHierarchyElement.addEventListener('click', function () {
    var lastSlashIndex = path.lastIndexOf('/');
    console.log(path)
    path = path.substring(0, lastSlashIndex);
    fetchRepositoryContent();
})

function readRile() {
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
            const cell = document.createElement('td');
            const text = document.createElement('pre')

            if (data !== '') {
                text.textContent = data;
            } else {
                text.classList.add("text-secondary");
                text.textContent = "Файл не содержит данных"
            }
            text.classList.add("text-document");
            text.classList.add("p-2");
            text.classList.add("m-0");

            cell.classList.add("m-0");
            cell.classList.add("p-0");

            cell.appendChild(text);
            row.appendChild(cell);
            repositoryContent.appendChild(row);


            if (path.split("/").length > 2) {
                upTheHierarchyElement.classList.remove("hidden")
            } else {
                upTheHierarchyElement.classList.add("hidden")
            }
        })
        .catch(error => console.error('Ошибка при загрузке содержимого репозитория:', error));
}









const downloadButton = document.getElementById('download-repository-archive');
downloadButton.addEventListener('click', function () {
    const jwtToken = localStorage.getItem('jwtToken');

    fetch(`/api/file/download/${userId_}/${repositoryName_}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка загрузки архива');
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${repositoryName_}.zip`;
            document.body.appendChild(a);
            a.click();
            a.remove();
        })
        .catch(error => console.error('Ошибка при загрузке архива репозитория:', error));

})



