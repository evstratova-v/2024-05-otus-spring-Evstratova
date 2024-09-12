import React from 'react'
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Edit from "./components/Edit";
import List from "./components/List";
import Add from "./components/Add";

const App = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<List/>}/>
                <Route path="/edit/:id" element={<Edit/>}/>
                <Route path="/add" element={<Add/>}/>
            </Routes>
        </BrowserRouter>);
}

export default App;
