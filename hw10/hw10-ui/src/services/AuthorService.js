const getAuthorsFromApi = () => {
    return fetch('/api/v1/author')
        .then(response => response.json())
}

export {getAuthorsFromApi}
