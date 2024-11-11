const getBooksFromApi = () => {
    return fetch('/api/v1/book')
        .then(response => response.json())
}

const getBookByIdFromApi = (id) => {
    return fetch('/api/v1/book/' + id)
        .then(response => response.json())
}

const createBookByApi = (book) => {
    return fetch("/api/v1/book", {
        method: "POST",
        body: JSON.stringify(book),
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
    })
}

const deleteBookByIdByApi = (id) => {
    return fetch(`/api/v1/book/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
}

const editBookByApi = (book) => {
    return fetch("/api/v1/book", {
        method: "PUT",
        body: JSON.stringify(book),
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },

    })
}

export {
    getBooksFromApi,
    getBookByIdFromApi,
    createBookByApi,
    editBookByApi,
    deleteBookByIdByApi
}
