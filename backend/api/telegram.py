from telethon import TelegramClient, events, sync
import os
from dotenv import load_dotenv

<<<<<<< HEAD
api_id = 4139893
api_hash = 'ece013c7c8869d3a584bb2cae5ae3fc7'
phone1 = '+79213010190'
=======
load_dotenv('../.env')
api_id = int(os.getenv('api_id'))
api_hash = os.getenv('api_hash')
phone = os.getenv('phone')
uid = os.getenv('uid')
>>>>>>> c7e69a432bed1a49ae2fde7eb5d93f4e00bc8c19


def auth(uid, phone, code=None, code_hash=None):
    client = TelegramClient('tg_sessions/' + str(uid), api_id, api_hash)
    client.connect()
    if code is None:
        return client.send_code_request(phone)
    else:
        return client.sign_in(phone=phone, code=code, phone_code_hash=code_hash)


class TgApi:

    def get_messages(self):
        pass

    def send_message(self, entity, message):
        pass

    def logout(self):
        pass


auth(uid, phone)

#auth(uid, phone, '<hash_code>')

#client = TelegramClient('tg_sessions/' + 'Andrew', api_id, api_hash)
#client.connect()
#print(client.get_dialogs())
