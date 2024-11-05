import base64
import hashlib
import json
import traceback
from datetime import datetime,timedelta
import uuid
from time import sleep
import jwt
from exceptions import *
from token_repo import *
import db_conn as dbc
ISS = "http://localhost:50051"
SECRET="SuPeR_SeCrEt_ChEiE_LuNgA_De_ToT"
MINUTES_EXP=60
##########################TOKEN#####################################
def genToken(id_user):
    time = datetime.utcnow()
    time = time + timedelta(minutes=MINUTES_EXP)
    exp_date = time
    claims = {
        "iss": ISS,
        "sub": id_user,
        "exp": exp_date,
        "jti": str(uuid.uuid4()),
    }
    token = jwt.encode(claims, SECRET, algorithm='HS256')
    return token

def refreshToken(token):
    decoded=decode_and_verify_token(token)
    time = datetime.utcnow()
    time = time + timedelta(minutes=MINUTES_EXP)
    exp_date = time
    claims = {
        "iss": ISS,
        "sub": decoded['sub'],
        "exp": exp_date,
        "jti": decoded['jti'],
    }
    token = jwt.encode(claims, SECRET, algorithm='HS256')
    return token




def decode_and_verify_token(token):
    blacklisted=False
    try:
        blocked_tokens = []
        for t in read_blacklist():
            blocked_tokens.append(t[1])

        if token not in blocked_tokens:
            decoded_token = jwt.decode(token, SECRET, algorithms=['HS256']) #semnatura e verificata aici
            if decoded_token['iss'] != ISS:
                write_to_blacklist(decoded_token['jti'], token)
                raise CustomError("ISS invalid!401")

            try:
                dbc.get_user_by_id(decoded_token['sub']) # verific daca sub e in db
            except Exception as es:
                raise CustomError("Acest sub nu e valid!401")

            return decoded_token
        else:
            blacklisted=True
            raise CustomError("Token invalid!401")
    except Exception as e:
        if blacklisted==False:
            try:
                count_dots= token.count(".")
                if count_dots==2:
                    _, payload, _ = token.split(".")
                    padding = len(payload) % 4
                    if padding:
                        payload += '=' * (4 - padding)

                    dec = base64.urlsafe_b64decode(payload).decode('utf-8')
                    decoded = json.loads(dec)
                    if decoded.get('jti'):
                        write_to_blacklist(decoded['jti'], token)
                    else:
                        write_to_blacklist(str(uuid.uuid4()), token)
                else:
                    write_to_blacklist(str(uuid.uuid4()), token)
            except Exception as ess:
                print("Error decoding that token")
                raise CustomError("Token invalid!401")
        if "Signature" in str(e):
            raise CustomError("Token expirat!401")
        raise CustomError(str(e))




##########################TOKEN#####################################

##########################VERIFY USER DATA#####################################
def verify_user_data(username, password, role):
    verifyUsername(username)
    verifyPassword(password)
    verifyRole(role)
    return True

def verifyPassword(password):
    if len(password) < 1:
        raise CustomError("Parola trebuie sa aiba minim 2 caractere!422")
    if len(password)>100:
        raise CustomError("Parola trebuie sa aiba maxim 50 caractere!422")
    return True


def verifyUsername(username):
    if len(username) < 1:
        raise CustomError("Username-ul trebuie sa aiba minim 2 caractere!422")
    if len(username)>50:
        raise CustomError("Username-ul trebuie sa aiba maxim 50 caractere!422")
    return True


def verifyRole(role):
    if role not in ["ADMIN","DOCTOR","PACIENT"]:
        raise CustomError("Rolul trebuie sa fie ADMIN, DOCTOR sau PACIENT!422")
    return True
##########################VERIFY USER DATA#####################################

def hash_password(password):
    sha256_hash = hashlib.sha256()
    sha256_hash.update(password.encode('utf-8'))
    hashed_password = sha256_hash.hexdigest()
    return hashed_password