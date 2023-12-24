const downloadButton = document.getElementById('download-repository-archive');
const uploadFiles = document.getElementById('upload-files-to-repository');

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




function showDeleteConfirmation() {
    const modal = document.getElementById("confirmationModal");
    modal.style.display = "block";

    const confirmDeleteButton = document.getElementById("confirmDeleteButton");
    const cancelDeleteButton = document.getElementById("cancelDeleteButton");

    confirmDeleteButton.addEventListener("click", function () {
        const jwtToken = localStorage.getItem('jwtToken');
        fetch(`/api/file/delete/${path}`, {
            method: "DELETE",
            headers: {
                'Authorization': `Bearer ${jwtToken}`
            }
        })
            .then(function (response) {
                if (response.ok) {
                    modal.style.display = "none";
                    removeLastPathPart();
                    fetchRepositoryContent();
                    showElement('repository-page-content');
                } else {
                    console.log(response.text())
                    console.error("Ошибка при удалении");
                }
            })
            .catch(function (error) {
                console.error("Произошла ошибка: " + error);
            });
    });

    cancelDeleteButton.addEventListener("click", function () {
        modal.style.display = "none";
    });
}

document.getElementById('delete-dir-or-file').addEventListener('click', function() {
    showDeleteConfirmation()
})

window.addEventListener("click", function (event) {
    const modal = document.getElementById("confirmationModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
});

uploadFiles.addEventListener('click', function () {
    const input = document.createElement('input');
    input.type = 'file';
    input.multiple = true;
    input.style.display = 'none';

    input.addEventListener('change', function (event) {
        const files = event.target.files;

        const formData = new FormData();
        for (let i = 0; i < files.length; i++) {
            formData.append('files', files[i]);
        }

        const jwtToken = localStorage.getItem('jwtToken');
        fetch(`/api/file/upload/${path}`, {
            method: 'POST',
            body: formData,
            headers: {
                'Authorization': `Bearer ${jwtToken}`
            }
        })
            .then(response => response.json())
            .then(data => {
                console.log('Files uploaded:', data);
                fetchRepositoryContent();
            })
            .catch(error => {
                console.error('Error uploading files:', error);
            });
    });
    input.click();
});
