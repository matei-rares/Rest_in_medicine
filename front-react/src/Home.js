// Home.js

import React, {useEffect, useState} from 'react';
import {Navigate, useNavigate} from 'react-router-dom';

const Home = () => {
    const navigate = useNavigate();
    let isTokenValid = false
    const [role,setRole] = useState()


    useEffect( () => {
        if (localStorage.getItem("token")) {
            const asyncu=async () => {

                const response = await fetch('http://localhost:5000/gate/authorize', {
                    method: 'GET',
                    headers: {
                        'Content-type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem("token")
                    }
                });
                const data = await response.json();
                if (response.status.toString()[0] === "2") {

                    switch (data.role) {
                        case 'PACIENT':
                            //navigate('/pacient');
                            navigate("/pacient")
                            break;
                        case 'DOCTOR':
                            navigate('/doctor');
                            break;
                        case 'ADMIN':
                            navigate('/admin');
                            break;
                        default:
                            navigate('/login');
                    }


                } else {

                    navigate("/login")
                }

            }
             asyncu()

        } else {
            navigate("/login")
        }

    }, [navigate]);


};

export default Home;

function saveToken(token) {
    localStorage.setItem("token", token);
}

function getToken() {
    return localStorage.getItem("token");
}

function validateToken(token) {

    return true
}

function getRole(token) {

    //return ""
    return "PACIENT"
    //return "doctor"
}