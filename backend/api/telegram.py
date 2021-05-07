from telethon import TelegramClient, events, sync
import os
from dotenv import load_dotenv

load_dotenv('../.env')
api_id = os.getenv('api_id')
api_hash = os.getenv('api_hash')
phone1 = os.getenv('phone')


def auth(uid, phone, code=None):
    client = TelegramClient('tg_sessions/' + str(uid), api_id, api_hash)
    if code is None:
        client.send_code_request(phone)
    else:
        client.sign_in(phone, code)
    client.start()


class TgApi:

    def get_messages(self):
        pass

    def send_message(self, entity, message):
        pass

    def logout(self):
        pass


auth('Andrew', phone1)
