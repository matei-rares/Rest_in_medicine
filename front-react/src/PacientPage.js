import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import Select from 'react-select';
import {useUser} from "./UserContext";








//todo la o cerere sa resalvez tokenu
const PacientPage = () => {
    const [fullDoctorList, setDoctorList] = useState();
    const [doctorOptions, setDoctorOptions] = useState();
    const [pacientDetails, setPacientDetails] = useState();
    const [pacientConsult,setPacientConsult]=useState();
    const [pacientApp, setPacientApp] = useState();

    const [viewState, setViewState] = useState(0);
    const {userName} = useUser(); // todo use userName in other components

    useEffect(() => {
        handleDoctorsData();
        handleViewData();
    }, []);

    const handleViewData = async (e) => {
        setViewState(0)
        const response = await fetch('http://localhost:5000/gate/viewPacientData', {
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
            setPacientDetails(data)
        } else {
            console.log(data)
        }
    }
    const handleDoctorsData = async (e) => {
        setViewState(3)
        const response = await fetch('http://localhost:5000/gate/viewAllDoctors', {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response.json();

        if (response.status.toString()[0] === "2") {
            console.log(data._embedded)

            setDoctorList(data._embedded.doctorDtoList)
        } else {
            console.log(data)
        }
    }
    const handleViewAppointmets = async (e) => {
        setViewState(1)

        const response = await fetch('http://localhost:5000/gate/viewPacientAppointments', {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response.json();
        if (response.status.toString()[0] === "2") {
            console.log(data)
            if("_embedded" in data){
            setPacientApp(data._embedded.programareDtoList)

            }
            else{
                setPacientApp()
            }
            //localStorage.setItem("token", data.token)
        } else {
            console.log(data)
        }


    }
    const handlePacientMedical = async (e) => {
        setViewState(2)
        const response = await fetch('http://localhost:5000/gate/viewPacientMedical', {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            }
        });
        const data = await response.json();
        if (response.status.toString()[0] === "2") {
            console.log(data._embedded.consultationDtoes)
            setPacientConsult(data._embedded.consultationDtoes)
            //localStorage.setItem("token", data.token)
        } else {
            console.log(data)
        }

    }

//////////////////////////////////
    const handleAppoint = async (e) => {
        console.log("trimit cerere cu date")
        console.log(selectedDoctor, appointDate)
        await makeAppointmet(selectedDoctor, appointDate)
    }

    const doctorSelect = (event) => {
        setSelectedDoctor(event.target.value);
        console.log(event.target.value)
        //todo am idul doctorului
    };


    const [appointDate, setAppointDate] = useState("2024-10-22");
    const [error, setError] = useState(null);
    const [selectedDoctor, setSelectedDoctor] = useState("");
    const [doctorSelectVisible, setDoctorSelectVisible] = useState(false);


    const secondSelectChange = (selectedOption) => {
        setDoctorSelectVisible(!!selectedOption.value);
        let specializare = selectedOption.value

        setDoctorOptions([])
        let doctorsList = []
        for (let i = 0; i < fullDoctorList.length; i++) {
            if (fullDoctorList[i].specializare === specializare) {
                doctorsList.push({
                    idUser: fullDoctorList[i].idUser,
                    name: fullDoctorList[i].nume + " " + fullDoctorList[i].prenume
                })
            }
        }
        console.log(doctorsList)
        setDoctorOptions(doctorsList)
    };

    const secondSelectOptions = [
        {value: '', label: '--Select--'},
        {value: 'NEUROLOG', label: 'Neurolog'},
        {value: 'UROLOG', label: 'Urolog'},
        {value: 'ONCOLOG', label: 'Oncolog'},
        {value: 'DERMATOLOG', label: 'Dermatolog'},
        {value: 'GINECOLOG', label: 'Ginecolog'},
        {value: 'ORTOPED', label: 'Ortoped'}

    ];
    const navigate = useNavigate();
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

    const getDoctorName=(id_doctor)=>{
        let dctr=fullDoctorList.filter(x=>x.idUser === id_doctor)
        return dctr[0].nume +" "+dctr[0].prenume
    }

    const deactivateUser=async () => {
        let id=pacientDetails.idUser
        const response = await fetch('http://localhost:5000/gate/api/medical_office/pacients/state/'+id, {
            method: 'PATCH',
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
    const toggleChangePassword=()=>{
        setViewState(4)
    }
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const handleChangePasword=async (e) => {
        e.preventDefault();
        const response = await fetch('http://localhost:5000/gate/changePassword', {
            method: 'PATCH',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem("token")
            },
            body: JSON.stringify({currentPassword,newPassword})
        });
        const data = await response.json();

        if (response.ok) {
            setError("Password changed succsefully")
        } else {
            setError(data.message)
        }

    }


    const [isConfirmationDialogOpen, setConfirmationDialogOpen] = useState(false);



    const handleCloseDialog = () => {
        setConfirmationDialogOpen(false);
    };

    const handleYesClick = () => {
        deactivateUser()

        handleCloseDialog();
    };

    const handleNoClick = () => {

        handleCloseDialog();
    };

    // Open the dialog when this function is called
    const openDialogOnClick = () => {
        setConfirmationDialogOpen(true);
    };




    return (
        <div>
            <h2>Hello pacient {userName}!</h2>
            <div style={{position: 'absolute', top: "30px", right: "30px"}}>
                <button onClick={() => handleLogout()}>Logout</button>
            </div>
            <button onClick={() => handleViewData()}> Profilul meu</button>
            <button onClick={() => handleViewAppointmets()}>Programarile mele</button>
            <button onClick={() => handlePacientMedical()}> Istoric medical</button>
            <button onClick={() => handleDoctorsData()}> Doctori</button>
            <br/>
            <br/>
            {viewState === 0 && pacientDetails !== undefined && (
                <div>
                    <h3>Informatii despre pacient</h3>
                    <label>Username: {userName}</label><br/>
                    <label>Nume: {pacientDetails.prenume}</label><br/>
                    <label>Prenume: {pacientDetails.nume}</label><br/>
                    <label>CNP: {pacientDetails.cnp}</label><br/>
                    <label>Data Nasterii: {pacientDetails.dataNasterii}</label><br/>
                    <label>Email: {pacientDetails.email}</label><br/><br/>
                    <button onClick={() => toggleChangePassword(pacientDetails.idUser)}>Schimba parola</button>
                    <br/>
                    <button onClick={() =>openDialogOnClick()}>Dezactiveaza cont</button>

                </div>
            )}

            {viewState === 1 && (
                <div>
                    <label style={{fontWeight: 'bold'}}>Creaza o programare</label><br/>
                    <label>
                        Data: <input type="datetime-local" required value={appointDate}
                                     onChange={(e) => setAppointDate(e.target.value)}/>
                    </label>

                    <br/>

                    <label>
                        Specializare: <Select  required options={secondSelectOptions} onChange={secondSelectChange}/>
                    </label>

                    {doctorSelectVisible && (
                        <div >
                            <label> Doctor:</label>
                            <select value={selectedDoctor} onChange={doctorSelect}>
                                <option value="">-- Select --</option>
                                {doctorOptions.map((option) => (
                                    <option key={option.idUser} value={option.idUser}>{option.name}</option>))}
                            </select>
                        </div>
                    )}

                    <br/>
                    <button onClick={() => handleAppoint()}>Creeaza programare</button>
                    <br/>

                    {error && <p style={{color: 'red'}}>{error}</p>}

                    {pacientApp !== undefined && (<h3>Programarile curente</h3>)}

                    {pacientApp !== undefined && pacientApp.map((x, index) => (
                        <div key={index}>
                            <label>Programarea {index + 1}</label><br/>
                            <label>Data: {x.data}</label><br/>
                            <label>Doctor: {getDoctorName(x.id_user_doctor)}</label><br/>
                            <label>Status: {x.status === null ? "in curand" : x.status.toLowerCase()}</label><br/>
                            <br/>
                        </div>
                    ))}
                    {pacientApp === undefined && (<h3>Nu exista Programari</h3>)}

                </div>
            )}

            {viewState === 2 && (
                <div>
                    <h3>Detalii despre programari</h3>
                    {pacientConsult !== undefined && pacientConsult.map((x, index) => (
                        <div key={index + 1}>
                            <label style={{fontWeight: 'bold'}}>Consultatia {index + 1}</label><br/>
                            <label>Data: {x.date}</label><br/>
                            <label>Doctor: {getDoctorName(x.id_doctor)}</label><br/>
                            <label>Diagnostic: {x.diagnostic}</label><br/>
                            {x.investigations.length > 0 && x.investigations.map((y, indexul) => (
                                <div key={index + 1 + "" + (indexul + 1)}>
                                    <label
                                        style={{fontWeight: 'bold'}}>Investigatia {index + 1 + "" + (indexul + 1)}</label><br/>
                                    <label>Denumire: {y.denumire}</label><br/>
                                    <label>Durata de procesare: {y.durata_de_procesare}</label><br/>
                                    <label>Rezultat: {y.rezultat}</label><br/>
                                    <br/>
                                </div>
                            ))}
                            <br/>
                        </div>
                    ))}

                    {pacientConsult === undefined && (<label>There are no consults</label>)}
                </div>
            )}

            {viewState === 3 && (
                <div>
                    <h3>Doctori</h3>

                    {fullDoctorList !== undefined && fullDoctorList.map((x, index) => (
                        <div key={index}>
                            <label>Doctor: {x.nume} {x.prenume}</label><br/>
                            <label>Specializare: {x.specializare} </label><br/>
                            <label>Telefon: {x.telefon} </label><br/>
                            <label>Email: {x.email} </label><br/>
                            <br/>
                        </div>
                    ))}
                    {fullDoctorList === undefined && (<h3>There are no doctors</h3>)}
                </div>
            )}

            {viewState === 4 && (
                <div>
                    <h3> Schimba parola</h3>
                    <form onSubmit={handleChangePasword}>
                        <label>
                            Parola curenta:
                            <input
                                type="password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                            />
                        </label><br/>
                        <label>
                            Parola noua:
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


            <div className="dialog">
                <ConfirmationDialog
                    isOpen={isConfirmationDialogOpen}
                    onClose={handleCloseDialog}
                    onYesClick={handleYesClick}
                    onNoClick={handleNoClick}
                />
            </div>
        </div>

    );
};


const ConfirmationDialog = ({ isOpen, onClose, onYesClick, onNoClick }) => {
    const overlayStyle = {
        display: isOpen ? 'flex' : 'none',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'fixed',
        top: 0,
        left: "-20px",
        width: '100%',
        height: '100%',
        backgroundColor: 'rgba(0, 0, 0, 0.5)', // Semi-transparent black overlay
    };

    const contentStyle = {
        background: 'white',
        padding: '20px',
        borderRadius: '5px',
        width: '300px',
        textAlign: 'center',
    };

    return (
        <div style={overlayStyle} onClick={onClose}>
            <div style={contentStyle} onClick={(e) => e.stopPropagation()}>
                <p>Esti sigur ca vrei sa iti dezactivezi contul ?</p>
                <button onClick={onYesClick}>Daa</button>
                <button onClick={onNoClick}>Nup</button>
            </div>
        </div>
    );
};
async function makeAppointmet(doctorId, date) {
    const response = await fetch('http://localhost:5000/gate/createAppointment', {
        method: 'POST',
        body: JSON.stringify({doctor_user_id: doctorId, date: date}),
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

export default PacientPage;