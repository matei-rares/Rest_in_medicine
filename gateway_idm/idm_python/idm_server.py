import grpc
from concurrent import futures
import time
import  idm_service_pb2 as idm_protobuf
import  idm_service_pb2_grpc as idm_grpc
import user_service as user_repo
from security import *
import db_conn as dbc

class IDMServicer(idm_grpc.IDMServiceServicer):
    def Authorize(self, request, context):
        try:
            token = refreshToken(request.token)
            decoded = decode_and_verify_token(token)
            dec_role = dbc.get_user_by_id(decoded['sub']).role
            return idm_protobuf.TokenResponse(token=token, message="Actiune autorizata cu success!200",role=dec_role,sub=str(decoded['sub']))
        except Exception as e:
            print(str(e))
            return idm_protobuf.TokenResponse(token="", message=str(e),role="",sub="")



    def CreateUser(self, request, context):
        try:
            verify_user_data(request.username,request.password,request.role)
            token,role=user_repo.create_user(request.token,request.username,request.password,request.role)
            sub=dbc.get_user_by_name(request.username).uid

            return idm_protobuf.TokenResponse(token=str(token),message="Created succesfully!201",role="",sub=str(sub))
        except Exception as e:
            print(str(e))
            return idm_protobuf.TokenResponse(token="",message=str(e),role="",sub="")


    def Login(self, request, context):
        try:
            token,role=user_repo.login_user(request.username,request.password)
            sub=dbc.get_user_by_name(request.username).uid
            return idm_protobuf.TokenResponse(token=token,message="User logat cu success!200",role=role,sub=str(sub))
        except Exception as e:
            print(str(e))
            return idm_protobuf.TokenResponse(token="",message=str(e),role="",sub="")
    def Logout(self, request, context):
        try:
            user_repo.logout(request.token)
            return idm_protobuf.StringResponse(message="User delogat cu success!200")
        except Exception as e:
            print(str(e))
            return idm_protobuf.StringResponse(message="User delogat cu success!200")

    def DeleteUser(self, request, context):
        try:
            user_repo.delete_user(request.token,request.id)
            return idm_protobuf.StringResponse(message="User sters cu success!204")
        except Exception as e:
            print(str(e))
            return idm_protobuf.StringResponse(message=str(e))



    def ChangePassword(self, request, context):
        try:
            verifyPassword(request.newPassword)
            user_repo.change_password(request.token,request.currentPassword,request.newPassword)
            token=refreshToken(request.token)
            return idm_protobuf.TokenResponse(token=token,message="Parola schimbata!200")
        except Exception as e:
            print(str(e))
            return idm_protobuf.TokenResponse(token="",message=str(e))



    def GetUsers(self, request, context):
        try:
            print(request.token)
            decoded = decode_and_verify_token(request.token)
            dec_role = dbc.get_user_by_id(decoded['sub']).role
            if dec_role == "ADMIN":
                users=dbc.get_all()

                user_list=idm_protobuf.UserList()
                for user in users:
                    user_message = idm_protobuf.User(
                        id=user.uid,
                        username=user.username,
                        role=user.role,
                    )
                    user_list.list.extend([user_message])
                user_list.message="Success!200"
                return user_list
            else:
                return idm_protobuf.UserList(message="You can't access this!403")
        except Exception as e:
            print(str(e))
            return idm_protobuf.UserList(message="You can't access this!403")



server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))

idm_grpc.add_IDMServiceServicer_to_server(
        IDMServicer(), server
)

print('Starting server. Listening on port 50051.')
server.add_insecure_port('[::]:50051')
server.start()
server.wait_for_termination()
