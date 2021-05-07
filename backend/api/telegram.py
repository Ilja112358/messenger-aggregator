from telethon import TelegramClient, events, sync
import os
import asyncio

api_id = 4139893
api_hash = 'ece013c7c8869d3a584bb2cae5ae3fc7'
phone1 = '+79213010190'


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
