from telethon import TelegramClient, events, sync
import os
from dotenv import load_dotenv
from api.protobufs import tg_pb2_grpc
from api.protobufs import tg_pb2
from api.protobufs import common_pb2
import asyncio

load_dotenv('.env')
api_id = int(os.getenv('api_id'))
api_hash = os.getenv('api_hash')

NUMBER_OF_MESSAGES = 200


class TgApiServicer(tg_pb2_grpc.TgApiServicer):
    def auth(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + request.uid, api_id, api_hash)
        client.connect()
        if request.code == '':
            response = common_pb2.AuthResponse(data=client.send_code_request(request.phone).__dict__['phone_code_hash'])
        else:
            client.sign_in(phone=request.phone, code=request.code, phone_code_hash=request.code_hash)
            response = common_pb2.AuthResponse(data='Test')

        client.disconnect()
        return response

    def get_dialogs(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + request.uid, api_id, api_hash)
        client.connect()
        temp_dialogs = client.get_dialogs()
        dialogs = []
        for temp_dialog in temp_dialogs:
            dialog_id = client.get_peer_id(temp_dialog)
            dialog = common_pb2.Dialog(name=temp_dialog.name, dialog_id=dialog_id, date=int(temp_dialog.date.timestamp()),
                                   message=temp_dialog.message.message, unread_count=temp_dialog.unread_count)
            dialogs.append(dialog)
        response = common_pb2.Dialogs(dialog=dialogs)
        client.disconnect()
        return response

    def get_messages(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + request.uid, api_id, api_hash)
        client.connect()
        messages = []
        temp_messages = client.get_messages(request.dialog_id, NUMBER_OF_MESSAGES)
        temp_dialogs = client.get_dialogs()
<<<<<<< HEAD
=======
        client.disconnect()
>>>>>>> parent of 16f9478 (Merge branch 'master' of https://github.com/Ilja112358/messenger-aggregator)
        name = ''
        if request.dialog_id > 0:
            for dialog in temp_dialogs:
                peer_id = client.get_peer_id(dialog)
                if peer_id == request.dialog_id:
                    name = dialog.name
        for temp_message in temp_messages:
            if temp_message.out == True:
                sender = 'me'
            else:
                if request.dialog_id > 0:
                    sender = name
                else:
<<<<<<< HEAD
                    sender = client.get_entity(temp_message.from_id.user_id).first_name + ' ' + client.get_entity(temp_message.from_id.user_id).last_name
            message = common_pb2.Message(message=temp_message.message, sender=sender, date=int(temp_message.date.timestamp()))
=======
                    sender = client.get_entity(temp_message.from_id.user_id).fisrt_name + ' ' + client.get_entity(temp_message.from_id.user_id).fisrt_name
            message = common_pb2.Message(message=temp_message.message, sender=sender, date=str(temp_message.date))
>>>>>>> parent of 16f9478 (Merge branch 'master' of https://github.com/Ilja112358/messenger-aggregator)
            messages.append(message)
        client.disconnect()
        response = common_pb2.Messages(message=messages)
        return response
    
    def send_message(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + str(request.uid), api_id, api_hash)
        client.connect()
        response = common_pb2.StatusMessage(status='FAIL')
        if isinstance(request.message, str):
            client.send_message(request.entity, request.message)
            response = common_pb2.StatusMessage(status='OK')
            client.disconnect()
            return response

    def mark_read(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + str(request.uid), api_id, api_hash)
        client.connect()
        response = common_pb2.StatusMessage(status='OK AND')
        client.send_read_acknowledge(request.dialog_id)
        client.disconnect()
        return response

    def test_file(self, request, context):
        with open('photo.jpg', 'rb') as f:
            file = f.read()
            # print(file)
            for byte in file:
                yield common_pb2.Chunk(chunk=byte.to_bytes(1, byteorder='big'))
