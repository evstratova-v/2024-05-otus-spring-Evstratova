const getGenresFromApi = () => {
    return fetch('/api/v1/genre')
        .then(response => response.json())
}

export {getGenresFromApi}
