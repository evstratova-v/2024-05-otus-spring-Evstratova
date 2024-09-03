import React, {useEffect, useState} from 'react'
import {Link} from "react-router-dom";

const styles = {
    booksTable: {
        border: "1px solid steelblue",
        width: "300px",
        borderCollapse: "collapse",
    },

    booksTableItem: {
        padding: "5px",
        border: "1px solid steelblue"
    }
}

const Header = (props) => (
    <h1>{props.title}</h1>
);

export default function List() {

    const [books, setBooks] = useState([]);

    const deleteBookByIdFromList = id => {
        console.log(`Удаляем из списка на странице книгу с id: ${id}`)
        setBooks(books => {
            return books.filter(book => book.id !== id)
        })
    }

    useEffect(() => {
        fetch('/api/v1/book')
            .then(response => response.json())
            .then(data => {
                console.log('Получили ответ от api со списком книг: ' + JSON.stringify(data));
                setBooks(data)
            });
    }, []);

    return (
        <>
            <Header title={'Books:'}/>
            <table style={styles.booksTable}>
                <thead>
                <tr style={styles.booksTableItem}>
                    <th style={styles.booksTableItem}>ID</th>
                    <th style={styles.booksTableItem}>Title</th>
                    <th style={styles.booksTableItem}>Author</th>
                    <th style={styles.booksTableItem}>Genres</th>
                    <th style={styles.booksTableItem}>Action</th>
                </tr>
                </thead>
                <tbody>
                {
                    books.map((book, i) => (
                        <tr style={styles.booksTableItem} key={i}>
                            <td style={styles.booksTableItem}>{book.id}</td>
                            <td style={styles.booksTableItem}>{book.title}</td>
                            <td style={styles.booksTableItem}>{book.author.fullName}</td>
                            <td style={styles.booksTableItem}>{
                                book.genres.map((genre, i) => (
                                    <div key={i}>{genre.name}</div>
                                ))
                            }
                            </td>
                            <td style={styles.booksTableItem}>
                                <Link to={`/edit/${book.id}`}>Edit</Link>
                                <button onClick={
                                    () => fetch(`/api/v1/book/${book.id}`, {
                                        method: 'DELETE',
                                        headers: {
                                            'Content-Type': 'application/json',
                                        }
                                    })
                                        .then(response => response.ok && deleteBookByIdFromList(book.id))
                                }>
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))
                }
                </tbody>
            </table>
            <Link to="/add">Add book</Link>
        </>
    )
};
