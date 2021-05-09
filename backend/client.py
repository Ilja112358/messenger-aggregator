import grpc
from api.protobufs import tg_pb2
from api.protobufs import common_pb2
from api.protobufs import tg_pb2_grpc
from api.protobufs import gmail_pb2_grpc

test_number = 2

channel = grpc.insecure_channel('84.252.137.106:6066')
tg_stub = tg_pb2_grpc.TgApiStub(channel)
gmail_stub = gmail_pb2_grpc.GmailApiStub(channel)

response = None
if test_number == 0:
    request = common_pb2.Send(uid='ilyich', entity='@durmankoo', message='Hello, Andrew!')
    response = tg_stub.send_message(request)
elif test_number == 1:
    request = common_pb2.Text()
    response = tg_stub.test_file(request)
    for item in response:
        pass
elif test_number == 2:
    request = common_pb2.User(uid='test')
    response = tg_stub.get_dialogs(request)
elif test_number == 3:
    request = common_pb2.DialogRequest(uid='test', dialog_id=-595779751)
    response = tg_stub.mark_read(request)
elif test_number == 4:
    request = common_pb2.DialogRequest(uid='test', dialog_id=-1001173526090)
    response = tg_stub.get_messages(request)
elif test_number == 5:
    request = common_pb2.User(uid='test')
    response = gmail_stub.auth(request)
elif test_number == 6:
    request = common_pb2.DialogRequest(uid='test')
    response = gmail_stub.get_dialogs(request)
elif test_number == 7:
    request = common_pb2.DialogRequest(uid='test', thread_id='1794cd87dca5fff6')
    response = gmail_stub.get_messages(request)

print(response)


