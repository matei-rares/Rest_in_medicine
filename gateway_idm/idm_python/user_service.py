from datetime import datetime, timedelta
import uuid
from time import sleep
import jwt
import db_conn as dbc
from security import *
from exceptions import *

ISS = "http://localhost:50051"
SECRET = "ok"
MINUTES_EXP = 5


def create_user(token, username, password, role):
    password = hash_password(password)
    if token:
        decoded = decode_and_verify_token(token)
        dec_role = dbc.get_user_by_id(decoded['sub']).role
        if dec_role == "ADMIN":
            response = dbc.add_user(username, password, role)
            return genToken(dbc.get_user_by_name(username).uid),role
        else:
            raise CustomError("You don't have permission to create users!")
    else:
        if role == "PACIENT":
            response = dbc.add_user(username, password, role)
            return genToken(dbc.get_user_by_name(username).uid),role
        else:
            raise CustomError("You don't have permission to create this type of users!")

def login_user(username,password):
    password=hash_password(password)
    user=dbc.get_user_by_name(username)
    if user.password==password:
        return genToken(user.uid),user.role
    else:
        raise CustomError("Wrong_password!401")

def logout(token):
    decoded=decode_and_verify_token(token)
    write_to_blacklist(decoded['jti'],token)
    return "ok"

def delete_user(token,userid):
    decoded = decode_and_verify_token(token)
    dec_role = dbc.get_user_by_id(decoded['sub']).role
    if dec_role == "ADMIN":
        return dbc.delete_user(userid)
    elif  userid==decoded['sub']:
        return dbc.delete_user(userid)
    raise CustomError("You don't have permission to delete users!")


def change_password(token,curr,new):
    decoded = decode_and_verify_token(token)
    user=dbc.get_user_by_id(decoded['sub'])
    if user.password==hash_password(curr):
        user.password=hash_password(new)
        user.save()
        return "ok"
    else:
        raise CustomError("Wrong current password!401")