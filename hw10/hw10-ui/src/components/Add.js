import React, {useEffect, useState} from 'react'
import {useNavigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import {createBookByApi} from "../services/BookService";
import {getAuthorsFromApi} from "../services/AuthorService";
import {getGenresFromApi} from "../services/GenreService";

const styles = {
    label: {
        display: "inline-block",
        width: "100px",
    },

    row: {
        marginTop: "10px",
    },

    inputReadOnly: {
        background: "lightgray",
    },

    error: {
        marginTop: "10px",
        color: "red",
    }
}

const Add = () => {

    const [book, setBook] = useState({title: "", authorId: 0, genresIds: []});

    const [authors, setAuthors] = useState([]);

    const [genres, setGenres] = useState([]);

    const navigate = useNavigate();

    const {
        register,
        formState: {errors},
        handleSubmit
    } = useForm({mode: "onBlur"});

    useEffect(() => {
        getAuthorsFromApi()
            .then(data => setAuthors(data))


        getGenresFromApi()
            .then(data => setGenres(data))
    }, []);

    const createBook = () => {
        console.log("Создаём книгу: " + JSON.stringify(book));
        createBookByApi(book)
            .then(response => {
                if (response.ok) {
                    navigate("/")
                }
            });
    }

    const handleChangeGenres = (e) => {
        const options = e.target.options;
        const value = [];
        for (let i = 0; i < options.length; i++) {
            if (options[i].selected) {
                value.push(options[i].value);
            }
        }
        setBook({...book, genresIds: value});
    }

    return (
        <form onSubmit={handleSubmit(createBook)} id="add-from">
            <h3>Book Info:</h3>

            <div style={styles.row} className="row">
                <label style={styles.label} htmlFor="title-input">Title:</label>
                <input id="title-input" name="title" type="text" value={book.title}
                       {...register("title", {
                           required: "Book title should not be empty",
                           minLength: {
                               value: 2,
                               message: "Minimum 2 symbols in title"
                           },
                           onChange: (e) => setBook({...book, title: e.target.value})
                       })}
                />
                <div style={styles.error}>
                    {errors?.title && <p>{errors?.title?.message || "Error!"}</p>}
                </div>
            </div>

            <div style={styles.row} className="row">
                <label style={styles.label} htmlFor="author-select">Author:</label>
                <select id="author-select" name="authorId"
                        {...register("authorId", {
                            required: "Book author should not be empty",
                            onChange: (e) => setBook({...book, authorId: e.target.value})
                        })}
                >
                    <option></option>
                    {
                        authors.map((author, i) => (
                            <option key={i} value={author.id}>{author.fullName}</option>
                        ))
                    }
                </select>
                <div style={styles.error}>
                    {errors?.authorId && <p>{errors?.authorId?.message || "Error!"}</p>}
                </div>
            </div>

            <div style={styles.row} className="row">
                <label style={styles.label} htmlFor="genres-ids-select">Genres:</label>
                <select id="genres-ids-select" name="genresIds" multiple
                        {...register("genresIds", {
                            required: "Genres should not be empty",
                            onChange: handleChangeGenres
                        })}
                >
                    {
                        genres.map((genre, i) => (
                            <option key={i} value={genre.id}>{genre.name}</option>
                        ))
                    }
                </select>
                <div style={styles.error}>
                    {errors?.genresIds && <p>{errors?.genresIds?.message || "Error!"}</p>}
                </div>
            </div>

            <div style={styles.row} className="row">
                <button type="submit">Save</button>
                <a href="/">
                    <button type="button">Cancel</button>
                </a>
            </div>

        </form>
    );
}

export default Add;
