import grpc


import idm_service_pb2
import idm_service_pb2_grpc


channel = grpc.insecure_channel('localhost:50051')

stub = idm_service_pb2_grpc.IDMServiceStub(channel)

#response = stub.SayHello(idm_service_pb2.HelloRequest(name='you'))

response=stub.CreateUser(idm_service_pb2.UserCreateRequest(username='onana',password='123456',token='',role="PACIENT"))

print(response)