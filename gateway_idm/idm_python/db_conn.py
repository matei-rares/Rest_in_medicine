import mysql.connector

from peewee import *
from exceptions import *


HOST = "localhost"
PORT = 3306
USER = "root"
PASSWORD = "password"
DATABASE = "idm"
db = MySQLDatabase(database=DATABASE, user=USER, password=PASSWORD, host=HOST, port=PORT)
db.connect()

class BaseModel(Model):
    class Meta:
        database = db

class User(BaseModel):
    uid = IntegerField(primary_key=True)
    username = CharField()
    password = CharField()
    role = CharField()

    class Meta:
        db_table = "AUTH"


def get_user_by_id(uid):
    try:
        user = User.get(User.uid == uid)
        return user
    except:
        raise CustomError("User not found!404")

def get_user_by_name(username):

    try:
        user = User.get(User.username == username)
        return user
    except:
        raise CustomError("User not found!404")


def add_user(username, password, role):
    user = User.get_or_none(User.username == username)
    if user:
       raise CustomError("User with this username already exists!409")
    else:
        user = User(username=username, password=password, role=role)
        user.save()
        return user


def delete_user(uid):
    user = get_user_by_id(uid)
    user.delete_instance()
    return "ok"


def get_all():
    users = User.select()
    # users=User.get()

    return users


def close_connection():
    db.close()

def change_password_by_username(username,new_password):
    user=get_user_by_name(username)
    user.password=new_password
    user.save()
    return "password_changed"


