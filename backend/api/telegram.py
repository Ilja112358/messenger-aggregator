from telethon import TelegramClient, events, sync
import os
from dotenv import load_dotenv
from api.protobufs import tg_pb2_grpc
from api.protobufs import tg_pb2
import asyncio

load_dotenv('.env')
api_id = int(os.getenv('api_id'))
api_hash = os.getenv('api_hash')


class TgApiServicer(tg_pb2_grpc.TgApiServicer):
    def auth(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        print(request, api_id, api_hash)
        client = TelegramClient('api/tg_sessions/' + request.uid, api_id, api_hash)
        print('Con')
        client.connect()
        print('After', request.code)
        if request.code == '':
            a = client.send_code_request(request.phone)
            response = tg_pb2.AuthResponse(data=a.__dict__['phone_code_hash'])
            print('Here 1')
            return response
            # return client.send_code_request(request.phone)
        else:
            client.sign_in(phone=request.phone, code=request.code, phone_code_hash=request.code_hash)
            response = tg_pb2.AuthResponse(data='Test')
            print('Here 2')
            return response

    def get_dialogs(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + request.uid, api_id, api_hash)
        client.connect()
        temp_dialogs = client.get_dialogs()
        dialogs = []
        for temp_dialog in temp_dialogs:
            dialog = tg_pb2.Dialog(name=temp_dialog.name, date=str(temp_dialog.date), message=temp_dialog.message.message)
            dialogs.append(dialog)
        response = tg_pb2.Dialogs(dialog=dialogs)
        return response

    def get_messages(self, request, context):
        pass

    def send_message(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + str(request.uid), api_id, api_hash)
        client.connect()
        if isinstance(request.message, str):
            client.send_message(request.entity, request.message)
            response = tg_pb2.StatusMessage(status='OK')
            return response