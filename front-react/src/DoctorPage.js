import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './Doctor.css';
import {useUser} from "./UserContext"; // Import the CSS file

const DoctorPage = () => {
    const [patients, setPatients] = useState([]);
    const [appointments, setAppointments] = useState([]);
    const navigate = useNavigate();
    const {userName} = useUser(); // todo use userName in other components
    const [viewState, setViewState] = useState(0);
    const [error, setError] = useState(null);


    useEffect(() => {
        handlePersonalInfo()
        handleGetPacients()
    }, []);

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
    const [doctorDetails, setDoctorDetails] = useState()
    const handlePersonalInfo = async () => {
        setViewState(0)
        setError("")
        const response = await fetch('http://localhost:5000/gate/viewDoctorDetails', {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        })

        const data = await response.json();
        if (response.status.toString()[0] === "2") {
            //todo save token
            //console.log(response.headers.get("X-New-Token"))
            //localStorage.setItem("token", data.token)

            console.log(data)
            setDoctorDetails(data)
        } else {
            if (data.message.includes("Token invalid")) {
                handleLogout()
            }
            console.log(data)
        }
    }
    const handleGetPacients = async (e) => {
        setViewState(1)
        setError("")
        const response = await fetch('http://localhost:5000/gate/viewOwnPacients', {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response.json();
        if (response.status.toString()[0] === "2") {
            console.log(data._embedded.pacientDtoList)
            //localStorage.setItem("token", data.token)
            setPatients(data._embedded.pacientDtoList);
        } else {
            console.log(data)
        }


    }
    const [selectedPatientId, setSelectedPatientId] = useState(null);

    const handleViewAppoint = async (patientId) => {
        console.log(`Viewing details for patient with ID ${patientId}`);
        const response = await fetch('http://localhost:5000/viewDoctorPacientApp/' + patientId, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response.json();
        if (response.status.toString()[0] === "2") {
            console.log(data._embedded.programareDtoList)
            //localStorage.setItem("token", data.token)
            setAppointments(data._embedded.programareDtoList);
            setSelectedPatientId(patientId);

        } else {
            console.log(data)
        }
    };
    const [selectedApp, setSelectedApp] = useState({data: null});
    const [shownConsult, setShownConsult] = useState({id: undefined, investigations: [""]});

    const seePacientCons = async (appoint,index) => {
        console.log("selected appointment ");
        console.log(appoint)
        setSelectedApp(index)
        let args = "?date=" + appoint.data + "&pacient_id=" + appoint.id_user_pacient
        const response = await fetch('http://localhost:5000/gate/seeConsult' + args, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });

        const data = await response.json()
        if (response.status.toString()[0] === "2") {
            console.log("response at consult");
            console.log(data)
            let consults
            if (data._embedded) {
                consults = data._embedded.consultationDtoes; //aici primesc si nr de pagini
            } else {
                consults = []
            }
            console.log(consults)
            if (consults.length === 0) {
                setShownConsult({id: undefined, investigations: [""]})
            } else {
                console.log(consults[0])
                setShownConsult(consults[0])

            }
            //localStorage.setItem("token", data.token)
        } else {
            console.log(data)
        }
    }
    const createConsult = async (appoint) => {
        appoint.diagnostic = "BOLNAV"
        const response = await fetch('http://localhost:5000/gate/createConsult', {
            method: 'POST',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            },
            body: JSON.stringify(appoint)

        });

        let data = await response.json();
        if (response.status.toString()[0] === "2") {
            console.log(data)
            //data = JSON.parse(data)
            if (data.length === 0) {
                console.log(data)
                setShownConsult({id: undefined, investigations: [""]})
            } else {
                console.log(data)
                setShownConsult(data)
                //variable=data
            }
            //localStorage.setItem("token", data.token)
        } else {
            console.log(data)
        }
    }

    const createInvestig = async (cons_id) => {
        console.log(investigationForm)
        console.log(shownConsult.id)
        const response = await fetch('http://localhost:5000/gate/createInvestigation', {
            method: 'POST',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            },
            body: JSON.stringify({
                consultation_id: shownConsult.id,
                denumire: investigationForm.value1,
                durata_de_procesare: investigationForm.value2,
                rezultat: investigationForm.value3
            })

        });

        const data = await response.json();
        if (response.status.toString()[0] === "2") {
            console.log(data)
            if (data.length === 0) {
                setShownConsult({id: undefined, investigations: [""]})
                setShowCreateInvestig(undefined)
            } else {
                setShownConsult(data)
                setShowCreateInvestig(undefined)
                //variable=data
            }
            //localStorage.setItem("token", data.token)
        } else {
            console.log(data)
        }
    }
    const modifyConsultStatus = async (event) => {
        console.log(event.target.value)
        setShownConsult({...shownConsult, diagnostic: event.target.value})
        const response = await fetch('http://localhost:5000/gate/api/consultations/'+shownConsult.id, {
            method: 'PATCH',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            },
            body: JSON.stringify({diagnostic: event.target.value})
        });

        const data = await response.json();

        if (response.ok) {
            console.log(data)
        } else {
        }




    }
    var diagnosticOptions = [{value: "1", name: "Bolnav"}, {value: "0", name: "Sanatos"}]

    const [showCreateInvestig, setShowCreateInvestig] = useState(false);
    const [investigationForm, setInvestigationForm] = useState({value1: "", value2: "", value3: ""})
    const toggleCreateInvestigation = () => {
        setShowCreateInvestig(!showCreateInvestig)
        setInvestigationForm({value1: "", value2: "", value3: ""})
    }
    const [modifInvestigationForm, setModifInvestigationForm] = useState({value1: "", value2: "", value3: ""})
    const [currInvestId, setCurrInvestId] = useState(-1);

    const prepareForChange = (invest) => {
        setCurrInvestId(invest.id)
        setModifInvestigationForm({
            value1: invest.denumire,
            value2: invest.durata_de_procesare,
            value3: invest.rezultat
        })
    }
    const handleModification = async (invest) => {
        for (let i = 0; i < shownConsult.investigations.length; i++) {
            if (invest.id === shownConsult.investigations[i].id) {
                console.log("modific valori")
                shownConsult.investigations[i].denumire = modifInvestigationForm.value1
                shownConsult.investigations[i].durata_de_procesare = modifInvestigationForm.value2
                shownConsult.investigations[i].rezultat = modifInvestigationForm.value3
                ///////////////////////////////////////////
                const response = await fetch('http://localhost:5000/gate/modifyConsult', {
                    method: 'PATCH',
                    headers: {
                        'Content-type': 'application/json',
                        'Authorization': 'Bearer ' + localStorage.getItem("token")
                    },
                    body: JSON.stringify({
                        inv_id: invest.id,
                        consultation_id: shownConsult.id,
                        denumire: modifInvestigationForm.value1,
                        durata_de_procesare: modifInvestigationForm.value2,
                        rezultat: modifInvestigationForm.value3
                    })

                });

                const data = await response.json();
                if (response.status.toString()[0] === "2") {
                    console.log(data)
                    //localStorage.setItem("token", data.token)
                } else {
                    console.log(data)
                }
                ///////////////////////////////////////////
            }
        }

        setCurrInvestId(-1)

    }
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');

    const handleChangePassword = async (e) => {
        e.preventDefault();
        const response = await fetch('http://localhost:5000/gate/changePassword', {
            method: 'PATCH',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            },
            body: JSON.stringify({currentPassword, newPassword})
        });
        const data = await response.json();

        if (response.ok) {
            setError("Password changed succsefully")
        } else {
            setError(data.message)
        }

    }

    const toggleChangePassword = () => {
        setViewState(4)
        setError("")
    }

    const deleteAppointment = (appoint) => {


    }
    const changeStatusApp = async (appoint, status) => {
        setStatusChangingIndex("")
        console.log(appoint)

        const response = await fetch('http://localhost:5000/gate/api/medical_office/doctors/'+doctorDetails.idUser+"/pacient/" +appoint.id_user_pacient+"/appointments", {
            method: 'PATCH',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            },
            body: JSON.stringify({data:appoint.data,status: status})
        });

        const data = await response.json();

        if (response.ok) {
            console.log(data)
        } else {
            console.log(data)
            console.log(error)
        }


    }
    const [statusChangingIndex, setStatusChangingIndex] = useState();

    const toogleStatusChanging = (index) => {
        setStatusChangingIndex(index)
    }

    return (
        <div>
            <h1>Salutari doctor {userName}!</h1>
            <div style={{position: 'absolute', top: "30px", right: "30px"}}>
                <button onClick={() => handleLogout()}>Logout</button>
            </div>

            <button onClick={() => handlePersonalInfo()}> Profil personal</button>

            <button onClick={() => handleGetPacients()}> Vizualizeaza pacienti</button>
            {viewState === 0 && doctorDetails !== undefined && (
                <div>
                    <h3>Detalii despre doctor</h3>
                    <label>Username: {userName}</label><br/>
                    <label>Nume: {doctorDetails.prenume}</label><br/>
                    <label>Prenume: {doctorDetails.nume}</label><br/>
                    <label>Specializare: {doctorDetails.specializare}</label><br/>
                    <label>Email: {doctorDetails.email}</label><br/>
                    <label>Telefon: {doctorDetails.telefon}</label><br/>
                    <br/>
                    <button onClick={() => toggleChangePassword(doctorDetails.idUser)}>Schimba parola</button>
                    <br/>

                </div>
            )}

            {viewState === 1 && (
                <div key={"pacienti"}>


                    {patients.length > 0 && (<h4>Pacientii tai</h4>)}
                    {patients.map((pacient, index) => (

                        <div key={index}>
                            <label style={{fontWeight: 'bold'}}>Pacient {index + 1}</label><br/>
                            <label>Nume: {pacient.nume} {pacient.prenume}</label><br/>
                            <label>Varsta: {calculateAge(pacient.dataNasterii)}</label><br/>
                            <label>Cnp: {pacient.cnp}</label><br/>
                            <label>Contact:  {pacient.email} (email)   {pacient.telefon} (telefon) </label>

                            <button onClick={() => handleViewAppoint(pacient.idUser)}>Vezi programari</button>
                            <br/>

                            {selectedPatientId === pacient.idUser && appointments.length > 0 && (
                                <label style={{fontWeight: 'bolder'}}>&nbsp;Appointments</label>)}
                            {selectedPatientId === pacient.idUser && appointments.length > 0 && appointments.map((appoint, index) => (

                                <div key={index}>
                                    <label style={{fontWeight: 'bold'}}>Programare {index + 1}:</label><br/>

                                    <pre>              Data:{appoint.data}</pre>

                                    {statusChangingIndex !== index && (
                                        <pre>              Status: {appoint.status === null ? "in curand" : appoint.status.toLowerCase()}</pre>)}

                                    {statusChangingIndex === index && (
                                        <pre>           Status: <select id="statusuri" >
                                                        <option value="0">Onorata</option>
                                                        <option value="1">Neprezentata</option>
                                                        <option value="2">Anulata</option>
                                                    </select><button
                                            onClick={() => {
                                                const selectedStatus = document.getElementById("statusuri").value;
                                                changeStatusApp(appoint,selectedStatus);
                                                appoint.status=determinteStatus(selectedStatus)}}>Save </button></pre>)}

                                    {statusChangingIndex !== index && (
                                        <pre>              <button onClick={() => toogleStatusChanging(index)}>Schimba status </button></pre>)}
                                    {/*
                                <pre>              <button onClick={() => deleteAppointment(appoint)}>Delete appointment</button></pre>
*/}                             { selectedApp !==index  && (
                                    <pre>              <button onClick={() => seePacientCons(appoint,index)}>Vezi consultatie</button></pre>
                                )}

                                    {selectedApp === index && shownConsult.id !== undefined && (
                                        <div key={"selectedAppointment"}>
                                            <label style={{fontWeight: 'bold'}}>Consultatie</label>
                                            <select
                                                 onChange={modifyConsultStatus}>
                                                {diagnosticOptions.map((option) => (
                                                    <option key={option.value}
                                                            value={option.value}>{option.name}</option>))}
                                            </select>

                                            {shownConsult.investigations.length === 0 && (
                                                <div key={"noInvestig"}>Nu exista nicio investigatie la consultatia asta</div>
                                            )}



                                            {shownConsult.investigations.length > 0 && shownConsult.investigations.map((invest, investIndex) => (

                                                <div key={invest.id}>
                                                    Investigatie {investIndex+1}
                                                    {currInvestId === invest.id ? (
                                                        <div key={"modifInvetigation"}>
                                                            <label className={"labelDreapta1"}>Denumire: </label>
                                                            <input value={modifInvestigationForm.value1}
                                                                   onChange={(e) => setModifInvestigationForm({
                                                                       ...modifInvestigationForm,
                                                                       value1: e.target.value
                                                                   })}/><br/>

                                                            <label className={"labelDreapta3"}>Durata de procesare: </label>
                                                            <input value={modifInvestigationForm.value2}
                                                                   onChange={(e) => setModifInvestigationForm({
                                                                       ...modifInvestigationForm,
                                                                       value2: e.target.value
                                                                   })}/><br/>
                                                            <label className={"labelDreapta2"}>Rezultat: </label>
                                                            <input value={modifInvestigationForm.value3}
                                                                   onChange={(e) => setModifInvestigationForm({
                                                                       ...modifInvestigationForm,
                                                                       value3: e.target.value
                                                                   })}/><br/>
                                                            <button onClick={() => handleModification(invest)}>
                                                                Save
                                                            </button>
                                                        </div>
                                                    ) : (
                                                        <div key={"ShowInvestig"}>
                                                            <label className={"labelDreapta1"}>Denumire: {invest.denumire}</label><br/>
                                                            <label>Durata de
                                                                procesare: {invest.durata_de_procesare}</label><br/>
                                                            <label>Rezultat: {invest.rezultat}</label><br/>

                                                            <button onClick={() => prepareForChange(invest)}>
                                                                Modifica Investigatie
                                                            </button>
                                                        </div>
                                                    )}

                                                </div>


                                            ))}

                                            {
                                                shownConsult.investigations.length >= 0 && (
                                                    <div key={"createInvestig"}>

                                                        {!showCreateInvestig && (
                                                        <button onClick={() => toggleCreateInvestigation()}>Adauga
                                                            investigatie
                                                        </button>)
                                                        }
                                                        {showCreateInvestig && (
                                                            <div key={"investigForm"}>
                                                                <label  style={{fontWeight: 'bold'}}>Creeaza o investigatie</label><br/>
                                                                <label className={"labelDreapta1"}>Denumire:</label>
                                                                <input  onChange={(event) => {
                                                                    setInvestigationForm({
                                                                        ...investigationForm,
                                                                        value1: event.target.value
                                                                    })
                                                                }}/><br/>
                                                                <label className={"labelDreapta3"}>Durata de procesare:</label>
                                                                <input onChange={(event) => {
                                                                    setInvestigationForm({
                                                                        ...investigationForm,
                                                                        value2: event.target.value
                                                                    })
                                                                }}/><br/>
                                                                <label className={"labelDreapta2"}>Rezultat:</label>
                                                                <input onChange={(event) => {
                                                                    setInvestigationForm({
                                                                        ...investigationForm,
                                                                        value3: event.target.value
                                                                    })
                                                                }}/><br/>
                                                                <button onClick={() => createInvestig()}>Adauga</button>
                                                            </div>
                                                        )}
                                                    </div>
                                                )

                                            }



                                        </div>
                                    )}

                                    {selectedApp === index && shownConsult.id === undefined && (
                                        <div key={"noConsults"}>Nu exista consultatii la programarea asta

                                            <button onClick={() => createConsult(appoint)}>Creeaza una</button>
                                        </div>
                                    )}

                                </div>

                            ))}
                        </div>
                    ))}
                    <br/>
                </div>
            )}


            {viewState === 4 && (
                <div>
                    <h3>Change password</h3>
                    <form onSubmit={handleChangePassword}>
                        <label>
                            Current Password
                            <input
                                type="password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                            />
                        </label><br/>
                        <label>
                            New Password
                            <input
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                            />
                        </label><br/>
                        <button type="submit">Save</button>
                    </form>
                </div>
            )}
            {error && <p style={{color: 'red'}}>{error}</p>}


        </div>
    );
};
export default DoctorPage;


function checkIfEmpty(array) {
    console.log(array)
    if (array.length === 0) {
        return true
    } else return false
}

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

function determinteStatus(number){
    switch(number){
case "0":
    return 'Onorata';
case "1":
    return 'Neprezentata';
case "2":
    return 'Anulata';
default:
    return 'Not a valid number';
}

}

async function medicalGetData() {
    const response = await fetch('http://localhost:5000/viewPacientMedical', {
        method: 'GET',
        headers: {
            'Content-type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem("token")
        }
    });
    const data = await response.json();
    if (response.status.toString()[0] === "2") {
        console.log(data)
        //localStorage.setItem("token", data.token)
    } else {
        console.log(data)
    }

}

