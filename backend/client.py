import grpc
from api.protobufs import tg_pb2
from api.protobufs import tg_pb2_grpc

channel = grpc.insecure_channel('localhost:6066')
stub = tg_pb2_grpc.TgApiStub(channel)
request = tg_pb2.User(uid='ilyich')
response = stub.get_dialogs(request)
print(response)
