import logo from './logo.svg';
//http://localhost:3000
import './App.css';

import React, {useEffect, useState} from 'react';
import {BrowserRouter as Router, Route, Routes, Navigate, useNavigate} from 'react-router-dom';
import Home from './Home';
import Login from './Login';
import PacientPage from './PacientPage';
import DoctorPage from "./DoctorPage";
import Register from "./Register";
import Admin from "./Admin";


const App = () => {
    return (
        <Router>
            <Routes>

                <Route path="/" element={<Home/>}/>

                <Route path="/login" element={<Login/>}/>
                <Route path="/pacient" element={<PacientPage/>}/>
                <Route path="/doctor" element={<DoctorPage/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/admin" element={<Admin/>}/>
            </Routes>
        </Router>
    );
};


export default App;


//todo in componentDidMount si in componentDidUpdate se fac cererile




