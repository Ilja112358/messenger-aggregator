from telethon import TelegramClient, events, sync
from telethon.tl.types import InputMessagesFilterPhotos


def out(username):
    api_id = 4139893
    api_hash = 'ece013c7c8869d3a584bb2cae5ae3fc7'
    client_session = 'sessions/' + username
    global client
    client = TelegramClient(client_session, api_id, api_hash)
    client.start()
out('Consumer')