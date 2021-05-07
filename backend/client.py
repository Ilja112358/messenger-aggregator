import grpc
from api.protobufs import tg_pb2
from api.protobufs import tg_pb2_grpc

channel = grpc.insecure_channel('localhost:6066')
stub = tg_pb2_grpc.TgApiStub(channel)
request = tg_pb2.AuthRequest(phone='+79219619133', uid='saasdfsascha')
response = stub.auth(request)
print(response.data)
