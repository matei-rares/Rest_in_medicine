import React, {Component, useEffect, useState} from "react";
import {useUser} from "./UserContext";
import {useNavigate} from "react-router-dom";


let adminId=""
const Admin =()=>{
    const [users, setUsers] = useState([]);
    const [appointments, setAppointments] = useState([]);
    const navigate = useNavigate();
    const {userName} = useUser();
    const [viewState, setViewState] = useState(0);
    const [error, setError] = useState(null);

    useEffect(() => {
    }, []);
    const getInfo=async () => {
        setViewState(1)
        const response = await fetch('http://localhost:5000/gate/view/users', {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response.json();
        if (response.status.toString()[0] === "2") {
            console.log(data)
            setUsers(data)
            getAllUserDetails()



        } else {
            console.log(data)
        }
    }

    const deleteUser=async (user) => {
        const response = await fetch('http://localhost:5000/gate/delete/'+user.id, {
            method: 'DELETE',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response;
        if (response.status.toString()[0] === "2") {
            const updatedUsers = users.filter(item => item.id !== user.id);
            setUsers(updatedUsers)
        } else {
            console.log(response.status)
        }
    }
    const handleLogout = async () => {
        const response = await fetch('http://localhost:5000/gate/logout', {
            method: 'POST',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response.json();
        if (response.status.toString()[0] === "2") {
            localStorage.clear()
            navigate("/")
        } else {
            localStorage.clear()
            navigate("/")
        }

    }

    const toggleCreateDoctor=()=>{
        setViewState(2)
    }
    const [formData, setFormData] = useState({
        username: 'user', password: 'a',  nume: "Andrunache", prenume: "andrew", email: "a@a.com", telefon: "0737540419", specializare: 0
    });
    const handleRegister = async (e) => {
        e.preventDefault();
        let initialPhone=formData.telefon
        formData.telefon="+4"+initialPhone;

        console.log(formData)
        try {

            const response = await fetch('http://localhost:5000/gate/registerDoctor', {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem("token")
                },
                body: JSON.stringify(formData)
            });
            const data = await response.json();
            console.log(response)
            formData.telefon=initialPhone
            if (response.ok) {
                console.log(data)
                setError("Doctor creat");
            } else {
                console.log(data.message)
                setError(data.message);
            }

        } catch (error) {
            console.error('Authentication error:', error.toString());
        }
    };
    const specialties = ['DERMATOLOG', 'NEUROLOG', 'UROLOG', 'ONCOLOG', 'GINECOLOG', 'ORTOPED'];
    const [selectedSpecialty, setSelectedSpecialty] = useState('');

    const handleChange = (event) => {
        console.log(event.target.value.split(" ")[1])
        setSelectedSpecialty(event.target.value);
        setFormData({...formData, epcializare: event.target.value.split(" ")[1]})
    };

    const getAllUserDetails=async () => {
        const response = await fetch('http://localhost:5000/gate/viewAllDoctors', {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response.json();

        if (response.ok) {
            console.log(data)
            let doctori=data._embedded.doctorDtoList //idUser
            console.log(doctori)
            setUsers((prevUsers) => {
                // Create a new array with the updated user object
                return prevUsers.map((user, i) => {
                    for (let i=0;i< doctori.length;i++){

                        if (user.id.toString() === doctori[i].idUser.toString()) {
                            return { ...user, details: doctori[i] };
                        }
                    }
                    return user;
                });
            });
        } else {
            console.log(data.message)
        }

        const response1 = await fetch('http://localhost:5000/gate/viewAllPacients', {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data1 = await response1.json();

        if (response1.ok) {
            let pacienti=data1._embedded.pacientDtoList
            console.log(data1)
            setUsers((prevUsers) => {
                return prevUsers.map((user, i) => {
                    for (let i=0;i< pacienti.length;i++){

                        if (user.id.toString() === pacienti[i].idUser.toString()) {
                            return { ...user, details: pacienti[i] };
                        }
                    }
                    return user;
                });
            });
        } else {
            console.log(data1.message)
        }
    }


    return (
        <div>
            <h3>Adminule {userName}</h3>
            <div style={{position: 'absolute', top: "30px", right: "30px"}}>
                <button onClick={() => handleLogout()}>Logout</button>
            </div>
            <button onClick={() => {
                getInfo()
            }}>Vezi utilizatori
            </button>
            <button onClick={()=>{toggleCreateDoctor()}}> Creeaza un doctor</button>

            { viewState ===1  && users !== undefined  && users.map((user, index) => (
                <div key={index}>
                    <label>Id: {user.id}</label><br/>
                    <label>Username: {user.username}</label><br/>
                    <label>Role: {user.role}</label><br/>

                    {user.details !== undefined && user.role === "PACIENT"  &&(
                        <div>
                            <label> Nume real: {user.details.nume} {user.details.prenume}</label><br/>
                            <label> Data
                                nasterii: {user.details.dataNasterii} ({calculateAge(user.details.dataNasterii)} ani)</label><br/>
                            <label> Cnp: {user.details.cnp}</label><br/>
                            <label> Email: {user.details.email}</label><br/>
                            <label> Telefon: {user.details.telefon}</label><br/>
                            <label> Cont activ: {user.details.isActive === true ? "Da" : "Nu"}</label><br/>
                        </div>
                    )}

                    {user.details !== undefined && user.role === "DOCTOR"  &&(
                        <div>
                            <label>{console.log(user.details)}</label>
                            <label> Nume real: {user.details.nume} {user.details.prenume}</label><br/>
                            <label> Specializare: {user.details.specializare}</label><br/>
                            <label> Telefon: {user.details.telefon}</label><br/>
                            <label> Email: {user.details.email}</label><br/>
                        </div>
                    )}

                    <button onClick={() => {
                        deleteUser(user)
                    }}>Sterge user
                    </button>

                    <br/>
                    <br/>

                </div>
            ))}
            {viewState === 1 && users.length <= 0  && (
                <div>
                    <label>Nu exista useri</label>
                </div>
            )}

            {viewState===2 && (
                <div>
                    <label>Creare doctor</label>
                    <form onSubmit={handleRegister}>


                        <label>Prenume:
                            <input type="text" maxLength={40} minLength={1} placeholder={formData.prenume}
                                   onChange={(e) => setFormData({...formData, firstname: e.target.value})}/>
                        </label>
                        <br/>

                        <label>Nume:
                            <input type="text" maxLength={40} minLength={1} placeholder={formData.nume}
                                   onChange={(e) => setFormData({...formData, lastname: e.target.value})}/>
                        </label>
                        <br/>

                        <label>Email:
                            <input type="email" placeholder={formData.email}
                                   onChange={(e) => setFormData({...formData, email: e.target.value})}/>
                        </label>
                        <br/>

                        <label>Telefon (+4):
                            <input type="tel" pattern="[0-9]+" maxLength={10} minLength={1} placeholder={formData.telefon}
                                   onChange={(e) => setFormData({...formData, phone: e.target.value})}/>
                        </label>
                        <br/>

                        <label>Specializare:
                            <select
                                id="specialtySelect"
                                value={selectedSpecialty}
                                onChange={handleChange}
                            >
                                <option value="" disabled>Selecteaza</option>
                                {specialties.map((specialty, index) => (
                                    <option key={index} value={specialty+" "+index }>{specialty}</option>
                                ))}
                            </select> </label>
                        <br/>

                        <label>Username:
                            <input type="text" placeholder={formData.username}
                                   onChange={(e) => setFormData({...formData, username: e.target.value})}/>
                        </label>
                        <br/>

                        <label>Password:
                            <input type="password" placeholder={formData.password}
                                   onChange={(e) => setFormData({...formData, password: e.target.value})}/>
                        </label>
                        <br/>

                        <button type="submit">Submit</button>

                        {error && <p style={{color: 'red'}}>{error}</p>}
                    </form>
                </div>


            )}

        </div>

    )
}
export default Admin;

function calculateAge(date){
    const today = new Date();
    const birthDate = new Date(date);

    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }

    return age;
}




async function logout() {
    const response = await fetch('http://localhost:5000/gate/logout', {
        method: 'POST',
        headers: {
            'Content-type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("token")
        }
    });
    const data = await response.json();
    if (response.status.toString()[0] === "2") {
        localStorage.clear()

    } else {
        localStorage.clear()
    }
}