// Import necessary modules
import React, {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useUser} from './UserContext';
import Register from "./Register";

const Login = () => {
    const [formData, setFormData] = useState({username: "doctor", password: "a"});
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const {login} = useUser();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('http://localhost:5000/login', {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json',
                    'Authorization': 'Bearer ' + ""
                },
                body: JSON.stringify(formData)
            });
            const data = await response.json();

            if (response.ok) {
                console.log(data.token)
                localStorage.setItem("token", data.token)
                login(formData.username);
                if (data.role === "PACIENT") {
                    const response = await fetch('http://localhost:5000/gate/viewPacientData', {
                        method: 'GET',
                        headers: {
                            'Content-type': 'application/json',
                            'Authorization': 'Bearer ' + localStorage.getItem("token")
                        }
                    })
                    const data = await response.json();
                    if (response.status.toString()[0] === "2") {
                        console.log(data)
                        if(data.isActive === true){
                            navigate('/pacient')
                        }
                        else{
                            setError("You are blocked")
                        }
                        return
                    } else {
                        setError("Error connecting to the server")
                        console.log(data)
                    }


                } else if (data.role === "DOCTOR") {
                    navigate("/doctor")
                }
                else if (data.role === "ADMIN"){
                    navigate("/admin")
                }
            } else {
                console.log(response.status)
                console.log(data)
                setError('Invalid name or password');
            }
        } catch (error) {
            console.error('Authentication error:', error);
            setError('Conn error');

        }
    };

    return (
        <div>
            <h1>Login Page</h1>

            <form onSubmit={handleLogin}>
                <label>
                    Username:
                    <input
                        type="text"
                        required
                        value={formData.username}
                        onChange={(e) => setFormData({...formData, username: e.target.value})}
                    />
                </label>
                <br/>
                <label>
                    Parola:
                    <input
                        type="password"
                        required
                        value={formData.password}
                        onChange={(e) => setFormData({...formData, password: e.target.value})}
                    />
                </label>
                <br/>
                <button type="submit">Login</button>
                <br/>
                <label>Nu ai un cont? </label>
                <Link to="/register">
                    <button type="button">Inregistreaza-te</button>
                </Link>
                {error && <p style={{color: 'red'}}>{error}</p>}
            </form>
        </div>
    );
};

export default Login;
