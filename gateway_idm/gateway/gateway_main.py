import json

import jwt
from flask import Flask, jsonify, request, Response, make_response
from flask_cors import CORS, cross_origin
import requests
import grpc
from spyne.protocol import yaml

import idm_service_pb2
import idm_service_pb2_grpc

SECRET = "SuPeR_SeCrEt_ChEiE_LuNgA_De_ToT"

try:
    channel = grpc.insecure_channel('localhost:50051')
    stub = idm_service_pb2_grpc.IDMServiceStub(channel)
    print("Connected to IDM service")
except Exception as e:
    print(e)


def idm_request_create_user(username, password, token, role):
    return stub.CreateUser(
        idm_service_pb2.UserCreateRequest(username=username, password=password, token=token, role=role))


def idm_request_delete_user(token, id):
    return stub.DeleteUser(idm_service_pb2.DeleteRequest(token=token, id=str(id)))


def idm_request_login(username, password):
    return stub.Login(idm_service_pb2.UserLoginRequest(username=username, password=password))


def idm_request_logout(token):
    return stub.Logout(idm_service_pb2.TokenRequest(token=token))


def idm_request_authorize(token):
    return stub.Authorize(idm_service_pb2.TokenRequest(token=token))


def get_id_from_token(token):
    decoded_token = jwt.decode(token, SECRET, algorithms=['HS256'])  # semnatura e verificata aici
    return decoded_token['sub']


def get_role_from_token(token):
    decoded_token = jwt.decode(token, SECRET, algorithms=['HS256'])  # semnatura e verificata aici
    return decoded_token['role']


def idm_request_change_pass(token, curr, new):
    return stub.ChangePassword(
        idm_service_pb2.UserChangePasswordRequest(token=token, currentPassword=curr, newPassword=new))


def idm_get_users(token):
    return stub.GetUsers(idm_service_pb2.TokenRequest(token=token))


# user: a, parola:a
# print(idm_request_create_user("a","a","","PACIENT"))
# print(idm_request_logout("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUwMDUxIiwic3ViIjo1LCJleHAiOjE3MDQyODQxMzcsImp0aSI6ImJiYzA0NGM1LTI3MDAtNGRkYS1iMjRmLTEzMmIwZWExM2M3NCIsInJvbGUiOiJQQUNJRU5UIn0.NxkCNc1rHp59w8UqOCzs9JkBroBYUVVShnZYNAzajwQ"))#todo rezolvre aici

app = Flask(__name__)
CORS(app, expose_headers='X-New-Token')

pacientUrl = "http://localhost:8080/api/medical_office/pacients/"
doctorUrl = "http://localhost:8080/api/medical_office/doctors/"
consultationUrl = "http://localhost:8081/api/consultations"
authorization_header = ""


# todo check if authorization exists acolo unde e folosit, verifica daca e autorizabil
@app.route('/gate/<path:subpath>', methods=['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD'])
def forward(subpath):
    print(request.method)
    print(subpath)
    authorization_header = request.headers.get('Authorization')
    if not authorization_header:
        return make_response(jsonify({"message": "No token provided!"}), 401)

    response = idm_request_authorize(str(authorization_header[7:]))
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({'message': response.message[0:-3], "token": response.token}),
                             response.message[-3:])
    header = {"Authorization": "Bearer " + str(response.token)}
    token = response.token
    sub = response.sub
    role = response.role
    if subpath == "authorize":
        return make_response({"message": response.message[:-3], "sub": sub, "role": role, "token": token},
                             response.message[-3:])

    if subpath == "":
        return make_response(jsonify({"message": "No path provided!"}), 404)

    response = "--"
    if request.method == "DELETE":
        if  "delete/" in subpath:
            sub=subpath.split("/")[-1]
            print(sub)
            return delete_user(token, sub)
    elif (request.method == "POST"):
        # print(request.json)
        if subpath == "registerDoctor":
            return createDoctor(token)
        if subpath == "createAppointment":
            response = createAppointment(header, sub)
        elif subpath == "createConsult":
            response = createConsult(header, sub)
        elif subpath == "createInvestigation":
            response = createInvestigation(header)
        elif "logout" in subpath:
            return logout(token)

    elif (request.method == "PATCH"):
        print(subpath)
        if subpath == "modifyConsult":
            response = modifyInvestigation(header)
        elif "api/medical_office/pacients/state" in subpath:
            pac_id = subpath.split("/")[-1]
            print(pac_id)
            response = change_pacient_state(header, pac_id)
        elif "changePassword" == subpath:
            return change_password(token)
        elif "api/consultations" in subpath:
            cons_id = subpath.split("/")[-1]
            response = changeConsStatus(header, cons_id)
        elif "api/medical_office/doctors" in subpath and "appointments" in subpath:
            pac_id = subpath.split("/")[-2]
            response = updateAppointment(header, sub, pac_id)
    else:
        print(subpath)
        if subpath == "view/users":
            return getAllUsers(token, sub)
        if subpath == "viewPacientData":
            response = view_pacient_data(header, sub)
        elif subpath == "viewPacientMedical":
            response = view_pacient_medical(header, sub)
        elif subpath == "viewPacientAppointments":
            response = view_pacient_appoint(header, sub)
        elif subpath == "viewAllDoctors":
            # todo cu query params
            # specializare = request.args.get('specializare')
            # name = request.args.get('name')
            # page = request.args.get('page')
            # items_per_page = request.args.get('items_per_page')
            # url=doctorUrl+f'?specializare={specializare}&name={name}&page={page}&items_per_page={items_per_page}'
            response = view_doctors(header)
        elif subpath == "viewAllPacients":
            # todo cu query params
            # specializare = request.args.get('specializare')
            # name = request.args.get('name')
            # page = request.args.get('page')
            # items_per_page = request.args.get('items_per_page')
            # url=doctorUrl+f'?specializare={specializare}&name={name}&page={page}&items_per_page={items_per_page}'
            response = view_pacients(header)
        elif subpath == "viewOwnPacients":
            response = viewOwnPacients(header, sub)
        elif subpath == "seeConsult":
            response = seeConsult(header, sub)
        elif "api/medical_office/doctors/" in subpath:
            doc_id = subpath.split("/")[-1]
            print(doc_id)
            response = getDoctorinfo(header, doc_id)
        elif "viewDoctorDetails" in subpath:
            response = getDoctorinfo(header, sub)

    if str(response) is None or str(response) == "--":
        return make_response(jsonify({"message": "Metoda nu a fost gasita"}), 404)

    if str(response.status_code)[0] == "4" or str(response.status_code)[0] == "5":
        return make_response(jsonify({"message": response.text}), response.status_code)
    response = make_response(jsonify(response.json()), response.status_code)
    response.headers['X-New-Token'] = token
    return response


def delete_user(token, id):
    response = idm_request_delete_user(token, id)
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({'message': response.message[0:-3]}), response.message[-3:])
    return make_response(jsonify({'message': response.message[0:-3]}), response.message[-3:])

def getAllUsers(token, sub):
    response = idm_get_users(token)
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({'message': response.message[0:-3]}),
                             response.message[-3:])
    users = str(response.list).split(",")
    users[0] = users[0].replace("[", " ")
    users[-1] = users[-1].replace("]", " ")
    for i in range(0, len(users)):
        users[i] = users[i].replace(" ", "").replace("\"", "").replace("\'", "").split("\n")
        users[i] = users[i][:-1]
        users[i][0] = users[i][0].replace("id:", "")
        users[i][1] = users[i][1].replace("username:", "")
        users[i][2] = users[i][2].replace("role:", "")

    dict = []
    for i in range(0, len(users)):
        if users[i][0] != str(sub):
            dict.append({"id": users[i][0], "username": users[i][1], "role": users[i][2]})

    return make_response(jsonify(dict), response.message[-3:])


def updateAppointment(header, sub, pac_id):
    data = request.json
    appointDate = data.get("data")
    status = data.get("status")
    print(status)
    return requests.patch(doctorUrl + str(sub) + "/pacient/" + str(pac_id) + "/appointments?date=" + appointDate, json={
        "status": status,
    }, headers=header)


@app.route('/registerPacient', methods=['POST'])
def createAccount():
    data = request.json
    response = idm_request_create_user(data["username"], data["password"], "", "PACIENT")
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({"message": response.message[0:-3]}), response.message[-3:])
    newToken = str(response.token)
    userid = str(response.sub)
    try:
        response = requests.put(pacientUrl, json={
            "cnp": data["cnp"],
            "idUser": userid,
            "nume": data["lastname"],
            "prenume": data["firstname"],
            "email": data["email"],
            "telefon": data["phone"],
            "dataNasterii": data["birthDate"],
            "isActive": True
        }, headers={"Authorization": "Bearer " + newToken})
    except Exception as e:
        print(e.with_traceback())
        idm_request_delete_user(newToken, userid)
        return make_response(jsonify({"message": "Invalid data!"}), 422)
    if str(response.status_code)[0] == "4" or str(response.status_code)[0] == "5":
        idm_request_delete_user(newToken, userid)
        return make_response(jsonify({"message": response.text}), response.status_code)

    return make_response(jsonify(response.json()), response.status_code)


def createDoctor(token):
    data = request.json
    response = idm_request_create_user(data["username"], data["password"], token=token, role="DOCTOR")
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({"message": response.message[0:-3]}), response.message[-3:])
    userid = str(response.sub)
    try:
        response = requests.put(doctorUrl, json={
            "idUser": userid,
            "nume": data["nume"],
            "prenume": data["prenume"],
            "email": data["email"],
            "telefon": data["telefon"],
            "specializare": data["specializare"]
        }, headers={"Authorization": "Bearer " + token})
    except Exception as e:
        print(e.with_traceback())
        idm_request_delete_user(token, userid)
        return make_response(jsonify({"message": "Invalid data!"}), 422)
    if str(response.status_code)[0] == "4" or str(response.status_code)[0] == "5":
        idm_request_delete_user(token, userid)
        return make_response(jsonify({"message": response.text}), response.status_code)

    return make_response(jsonify(response.json()), response.status_code)


@app.route('/login', methods=['POST'])
def authorizeLogin():
    data = request.json
    response = idm_request_login(data["username"], data["password"])
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({'message': response.message[0:-3]}), response.message[-3:])
    return make_response(jsonify(
        {'message': response.message[0:-3], "token": response.token, "role": response.role, "sub": response.sub}),
        response.message[-3:])


def change_password(token):
    data = request.json
    print(data)
    if data["newPassword"] == "" or data["currentPassword"] == "":
        return make_response(jsonify({'message': "Passwords cannot be empty!"}), 422)

    response = idm_request_change_pass(token, data["currentPassword"], data["newPassword"])
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({'message': response.message[0:-3]}), response.message[-3:])
    return make_response(jsonify(
        {'message': response.message[0:-3]}), response.message[-3:])


def logout(token):
    response = idm_request_logout(token)
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({'message': response.message[0:-3]}), response.message[-3:])
    return make_response(jsonify(
        {'message': response.message[0:-3]}), response.message[-3:])


def change_pacient_state(header, userid):
    return requests.patch(pacientUrl + "state/" + str(userid), json={
        "isActive": False
    }, headers=header)


def getDoctorinfo(header, doc_id):
    return requests.get(doctorUrl + str(doc_id), headers=header)


def view_pacient_data(header, userid):
    return requests.get(pacientUrl + str(userid), headers=header)


def view_pacient_medical(header, userid):
    return requests.get(consultationUrl + "?pacient=" + str(userid), headers=header)


def view_pacient_appoint(header, userid):
    return requests.get(pacientUrl + str(userid) + "/appointments", headers=header)


def view_doctors(header):
    return requests.get(doctorUrl, headers=header)
def view_pacients(header):
    return requests.get(pacientUrl, headers=header)


def createAppointment(header, userid):
    data = request.json
    doctor_id = data.get("doctor_user_id")
    appointDate = data.get("date")
    return requests.post(pacientUrl + str(userid) + "/appointments", json={
        "data": appointDate,
        "id_user_doctor": doctor_id,
        "id_user_pacient": userid
    }, headers=header)


def viewOwnPacients(header, userid):
    return requests.get(doctorUrl + str(userid) + "/pacients", headers=header)


@app.route("/viewDoctorPacientApp/<patientId>", methods=['GET'])
def viewDoctorPacientApp(patientId):
    authorization_header = request.headers.get('Authorization')
    if not authorization_header:
        return make_response(jsonify({"message": "No token provided!"}), 401)

    response = idm_request_authorize(str(authorization_header[7:]))
    if response.message[-3] == "4" or response.message[-3] == "5":
        return make_response(jsonify({'message': response.message[0:-3], "token": response.token}),
                             response.message[-3:])
    header = {"Authorization": "Bearer " + str(response.token)}

    doctor_id = get_id_from_token(str(request.headers.get('Authorization')[7:]))
    response = requests.get(doctorUrl + str(doctor_id) + "/pacient/" + str(patientId) + "/appointments", headers=header)
    if str(response.status_code)[0] == "4" or str(response.status_code)[0] == "5":
        return make_response(jsonify({"message": response.text}), response.status_code)

    return make_response(jsonify(response.json()), response.status_code)


def seeConsult(header, doctorid):
    date = request.args.get('date')  # 2026-12-15T12:59:11.300 00:00
    pacient_id = request.args.get('pacient_id')
    # search cu date si iduri in mongo
    params = {"pacient": pacient_id, "doctor": doctorid, "date": date}
    return requests.get(consultationUrl, headers=header, params=params)


def createConsult(header, doctor_id):
    data = request.json
    pacient_id = data.get("id_user_pacient")
    appointDate = data.get("data")
    print(str(appointDate))
    diagnostic = data.get("diagnostic")
    print(pacient_id)
    return requests.post(consultationUrl, json={
        "id_pacient": pacient_id,
        "id_doctor": doctor_id,
        "date": appointDate,
        "diagnostic": diagnostic
    }, headers=header)


def createInvestigation(header):
    data = request.json
    consultation_id = data.get("consultation_id")
    denumire = data.get("denumire")
    durata_de_procesare = data.get("durata_de_procesare")
    rezultat = data.get("rezultat")

    body = {
        "denumire": denumire,
        "durata_de_procesare": durata_de_procesare,
        "rezultat": rezultat
    }
    return requests.post(consultationUrl + "/" + str(consultation_id), json=body, headers=header)


def changeConsStatus(header, cons_id):
    data = request.json
    status = data.get("diagnostic")
    return requests.patch(consultationUrl + "/" + str(cons_id), json={"diagnostic": status},
                          headers=header)


def modifyInvestigation(header):
    data = request.json
    inv_id = data.get("inv_id")
    consultation_id = data.get("consultation_id")
    denumire = data.get("denumire")
    durata_de_procesare = data.get("durata_de_procesare")
    rezultat = data.get("rezultat")

    body = {
        "id": inv_id,
        "denumire": denumire,
        "durata_de_procesare": durata_de_procesare,
        "rezultat": rezultat
    }
    return requests.patch(consultationUrl + "/" + str(consultation_id) + "/update", json=body, headers=header)


# http://localhost:5000/hello
if __name__ == '__main__':
    app.run(debug=True)
