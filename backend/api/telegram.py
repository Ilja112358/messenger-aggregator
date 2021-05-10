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
WEB_PATH = '/var/www/html/'


def tg_decorator(func):
    def wrapper(self, request, context):
        asyncio.set_event_loop(asyncio.new_event_loop())
        client = TelegramClient('api/tg_sessions/' + request.uid, api_id, api_hash)
        client.connect()
        return func(self, request, context, client)
    return wrapper


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

    @tg_decorator
    def get_dialogs(self, request, context, client):
        temp_dialogs = client.get_dialogs()

        dialogs = []
        for temp_dialog in temp_dialogs:
            dialog_id = client.get_peer_id(temp_dialog)
            str_dialog_id = str(dialog_id)
            if not os.path.exists(WEB_PATH + 'avatars/' + str_dialog_id + '.jpg'):
                avatar_url = ''
                if client.download_profile_photo(dialog_id, WEB_PATH + 'avatars/' + str_dialog_id + '.jpg') is not None:
                    avatar_url = 'http://84.252.137.106/avatars/' + str_dialog_id + '.jpg'
            else:
                avatar_url = 'http://84.252.137.106/avatars/' + str_dialog_id + '.jpg'
            dialogs.append(common_pb2.Dialog(name=temp_dialog.name, dialog_id=dialog_id,
                                             date=int(temp_dialog.date.timestamp()),
                                             message=temp_dialog.message.message, unread_count=temp_dialog.unread_count,
                                             avatar_url=avatar_url))

        client.disconnect()
        return common_pb2.Dialogs(dialog=dialogs)

    @tg_decorator
    def get_messages(self, request, context, client):
        messages = []
        temp_messages = client.get_messages(request.dialog_id, NUMBER_OF_MESSAGES)
        dialog_entity = client.get_entity(request.dialog_id)

        name = ''
        if request.dialog_id > 0 and type(dialog_entity).__name__ != 'Channel':
            name = 'not me'

        for temp_message in temp_messages:
            media_type = ''
            media_url = ''
            if temp_message.media is not None:
                if type(temp_message.media).__name__ == 'MessageMediaPhoto':
                    media_type = 'photo'
                    photo_path = WEB_PATH + 'photos/' + str(temp_message.media.photo.id) + '.jpg'
                    if not os.path.exists(photo_path):
                        client.download_media(temp_message, photo_path)
                    media_url = 'http://84.252.137.106/photos/' + str(temp_message.media.photo.id) + '.jpg'
                elif type(temp_message.media).__name__ == 'MessageMediaDocument':
                    mime_type = temp_message.media.document.mime_type.split("/")
                    if (mime_type[0] in ['text', 'application']) and mime_type[1] != 'x-tgsticker':
                        media_type = 'file'
                        extension = '.' + temp_message.media.document.attributes[0].file_name.split('.')[-1]
                        file_path = WEB_PATH + 'files/' + str(temp_message.media.document.id) + extension
                        if not os.path.exists(file_path):
                            client.download_media(temp_message, file_path)
                        media_url = 'http://84.252.137.106/files/' + str(temp_message.media.document.id) + extension

            attachment = common_pb2.Attachment(type=media_type, url=media_url)

            sender = ''
            if type(dialog_entity).__name__ != 'Channel':
                sender = 'me'
                if not temp_message.out:
                    if request.dialog_id > 0:
                        sender = name
                    else:
                        entity = client.get_entity(temp_message.from_id.user_id)
                        sender = entity.first_name + ' ' + entity.last_name

            message = common_pb2.Message(message=temp_message.message, sender=sender,
                                         date=int(temp_message.date.timestamp()), attachment=attachment)
            messages.append(message)

        client.disconnect()

        return common_pb2.Messages(message=messages)

    @tg_decorator
    def send_message(self, request, context, client):
        response = common_pb2.StatusMessage(status='FAIL')
        if isinstance(request.message, str):
            client.send_message(request.dialog_id, request.message)
            client.disconnect()
            response = common_pb2.StatusMessage(status='OK')
        return response

    @tg_decorator
    def mark_read(self, request, context, client):
        client.send_read_acknowledge(request.dialog_id)
        client.disconnect()
        return common_pb2.StatusMessage(status='OK AND')

    @tg_decorator
    def get_id_by_username(self, request, context, client):
        try:
            entity = client.get_entity(request.username)
            uid = entity.id
        except ValueError:
            uid = 0
        return common_pb2.UserId(uid=uid)