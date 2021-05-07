from telethon import TelegramClient, events, sync
import os
from dotenv import load_dotenv

load_dotenv('../.env')
api_id = int(os.getenv('api_id'))
api_hash = os.getenv('api_hash')
phone = os.getenv('phone')
uid = os.getenv('uid')


class TgApi:
    def auth(self, uid, phone, code=None, code_hash=None):
        client = TelegramClient('tg_sessions/' + str(uid), api_id, api_hash)
        client.connect()
        if code is None:
            return client.send_code_request(phone)
        else:
            return client.sign_in(phone=phone, code=code, phone_code_hash=code_hash)

    def get_messages(self):
        pass

    def send_message(self, uid, entity, message):
        client = TelegramClient('tg_sessions/' + str(uid), api_id, api_hash)
        client.connect()
        if isinstance(message, str):
            client.send_message(entity, message)

    def logout(self):
        pass


# auth(uid, phone)

# auth(uid, phone, '<hash_code>')

#client = TelegramClient('tg_sessions/' + uid, api_id, api_hash)
#client.connect()
#print(client.get_dialogs())
