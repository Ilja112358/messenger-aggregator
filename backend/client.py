import grpc
from api.protobufs import tg_pb2
from api.protobufs import common_pb2
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
request = common_pb2.DialogRequest(uid='test', dialog_id=-595779751)
response = tg_stub.mark_read(request)
print(response)


