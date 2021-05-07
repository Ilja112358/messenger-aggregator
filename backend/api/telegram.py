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
            response = tg_pb2.AuthResponse(data=client.send_code_request(request.phone).phone_code_hash)
            print('Here 1')
            return response
            # return client.send_code_request(request.phone)
        else:
            response = tg_pb2.AuthResponse(data='Test')
            print('Here 2')
            return response
            # return client.sign_in(phone=request.phone, code=request.code, phone_code_hash=request.code_hash)

    def get_messages(self, request, context):
        pass

    def send_message(self, request, context):
        client = TelegramClient('tg_sessions/' + str(uid), api_id, api_hash)
        client.connect()
        if isinstance(request.message, str):
            client.send_message(request.entity, request.message)

    def logout(self):
        pass
