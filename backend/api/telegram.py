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
            if not os.path.exists('avatars/' + str(dialog_id) + '.jpg'):
                if client.download_profile_photo(dialog_id, 'avatars/' + str(dialog_id) + '.jpg') == None:
                    avatar_url = ''
                else:
                    avatar_url = 'http://84.252.137.106/avatars/' + str(dialog_id) + '.jpg'
            else:
                avatar_url = 'http://84.252.137.106/avatars/' + str(dialog_id) + '.jpg'
            dialogs.append(common_pb2.Dialog(name=temp_dialog.name, dialog_id=dialog_id,
                                     date=int(temp_dialog.date.timestamp()),
                                     message=temp_dialog.message.message, unread_count=temp_dialog.unread_count,
                                     avatar_url=avatar_url))

        response = common_pb2.Dialogs(dialog=dialogs)
        client.disconnect()
        return response

    def get_messages(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + request.uid, api_id, api_hash)
        client.connect()
        messages = []
        temp_messages = client.get_messages(request.dialog_id, NUMBER_OF_MESSAGES)
        name = ''
        if str(type(client.get_entity(request.dialog_id))) == "<class 'telethon.tl.types.Channel'>":
            sender = client.get_entity(request.dialog_id).title
        if request.dialog_id > 0 and str(type(client.get_entity(request.dialog_id))) != "<class 'telethon.tl.types.Channel'>":
            name = 'not me'
        for temp_message in temp_messages:
            file_type = ''
            file_url = ''
            if temp_message.media is not None:
                if str(type(temp_message.media)) == "<class 'telethon.tl.types.MessageMediaPhoto'>":
                    file_type = 'photo'
                    if not os.path.exists('/var/www/html/photos/' + str(temp_message.media.photo.id) + '.jpg'):
                        client.download_media(temp_message, '/var/www/html/photos/' + str(temp_message.media.photo.id) + '.jpg')
                    file_url = 'http://84.252.137.106/photos/' + str(temp_message.media.photo.id) + '.jpg'
                elif str(type(temp_message.media)) == "<class 'telethon.tl.types.MessageMediaDocument'>":
                    temp_mime_type = temp_message.media.document.mime_type.split("/")
                    if (temp_mime_type[0] == 'text' or temp_mime_type[0] == 'application') and temp_mime_type[1] != 'x-tgsticker':
                        file_type = 'file'
                        temp_file_type_mas = temp_message.media.document.attributes[0].file_name.split(".")
                        temp_file_type_mas.reverse()
                        temp_file_type = '.' + temp_file_type_mas[0]
                        if not os.path.exists('/var/www/html/files/' + str(temp_message.media.document.id) + temp_file_type):
                            client.download_media(temp_message,
                                                  '/var/www/html/files/' + str(temp_message.media.document.id) + temp_file_type)
                        file_url = 'http://84.252.137.106/files/' + str(temp_message.media.document.id) + temp_file_type
            attachment = common_pb2.Attachment(type=file_type, url=file_url)

            if str(type(client.get_entity(request.dialog_id))) != "<class 'telethon.tl.types.Channel'>":
                if temp_message.out == True:
                    sender = 'me'
                else:
                    if request.dialog_id > 0:
                        sender = name
                    else:
                        sender = client.get_entity(temp_message.from_id.user_id).first_name + ' ' + client.get_entity(temp_message.from_id.user_id).last_name
            message = common_pb2.Message(message=temp_message.message, sender=sender, date=int(temp_message.date.timestamp()), attachment=attachment)
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
