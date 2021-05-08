import grpc
from api.protobufs import tg_pb2
from api.protobufs import tg_pb2_grpc


channel = grpc.insecure_channel('localhost:6066')
tg_stub = tg_pb2_grpc.TgApiStub(channel)
# gmail_stub = gmail_pb2_grpc.GmailApiStub(channel)
'''
request = tg_pb2.Send(uid='ilyich', entity='@durmankoo', message='Hello, Andrew!')
response = tg_stub.send_message(request)
print(response)
'''
'''
request = tg_pb2.Text()
response = tg_stub.test_file(request)
for item in response:
    pass
'''
request = tg_pb2.User(uid='ilyich')
response = tg_stub.get_dialogs(request)
print(response)


