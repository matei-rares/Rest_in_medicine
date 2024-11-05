// Import necessary modules
import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {useUser} from './UserContext';

const Login = () => {
    const [formData, setFormData] = useState({
        username: 'user', password: 'a', cnp: "0190308470347", firstname: "matei", lastname: "rares", email: "a@a.com", phone: "0712345678", birthDate: "2001-13-11"
    });
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const {login} = useUser(); // todo use userName in other components

    const handleRegister = async (e) => {
        e.preventDefault();
        let initialPhone=formData.phone
        formData.phone="+4"+initialPhone;

        console.log(formData)
        try {

            const response = await fetch('http://localhost:5000/registerPacient', {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json',
                    'Authorization': 'Bearer ' + ""
                },
                body: JSON.stringify(formData)
            });
            const data = await response.json();
            formData.phone=initialPhone
            if (response.ok) {
                setError("User creat cu succes")

                setTimeout(() => {
                    navigate("/")
                }, 3000)
                console.log(data)
                //todo save token,name and redirect
                // navigate('/pacient');
                // login('name');
            } else {
                //todo logic pentru error
                setError(data.message);
            }

        } catch (error) {
            console.error('Authentication error:', error);
        }
    };

    return (
        <div>
            <h1>Inregistrare</h1>
            <form onSubmit={handleRegister}>
                <label>CNP:
                    <input type="text" maxLength={13} minLength={1} placeholder={formData.cnp} required
                           onChange={(e) => setFormData({...formData, cnp: e.target.value})}/>
                </label>
                <br/>

                <label>Prenume:
                    <input type="text" maxLength={40} minLength={1} placeholder={formData.firstname}
                           onChange={(e) => setFormData({...formData, firstname: e.target.value})}/>
                </label>
                <br/>

                <label>Nume:
                    <input type="text"  maxLength={40} minLength={1} placeholder={formData.lastname}
                           onChange={(e) => setFormData({...formData, lastname: e.target.value})}/>
                </label>
                <br/>

                <label>Email:
                    <input type="email" placeholder={formData.email}
                           onChange={(e) => setFormData({...formData, email: e.target.value})}/>
                </label>
                <br/>

                <label>Telefon (+4):
                    <input type="tel" pattern="[0-9]+" maxLength={10} minLength={1} placeholder={formData.phone}
                           onChange={(e) => setFormData({...formData, phone:e.target.value})}/>
                </label>
                <br/>

                <label>Data de nastere:
                    <input type="date" placeholder="1999-01-01"
                           onChange={(e) => setFormData({...formData, birthDate: e.target.value})}/>
                </label>
                <br/>

                <label>Username:
                    <input type="text" placeholder={formData.username}
                           onChange={(e) => setFormData({...formData, username: e.target.value})}/>
                </label>
                <br/>

                <label>Parola:
                    <input type="password" placeholder={formData.password}
                           onChange={(e) => setFormData({...formData, password: e.target.value})}/>
                </label>
                <br/>

                <button type="submit">Submit</button>

                {error && <p style={{color: 'red'}}>{error}</p>}
            </form>
        </div>
    );
};

function verifyData(){

}


export default Login;
