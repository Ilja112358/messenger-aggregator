from telethon import TelegramClient, events, sync
from telethon.tl.types import InputMessagesFilterPhotos


#user authorizathion
phone = '+79992083343'
api_id = 4139893
api_hash = 'ece013c7c8869d3a584bb2cae5ae3fc7'
client = TelegramClient('sessions/Consumer', api_id, api_hash)
client.start()

test_user = '@durmankoo'
client.send_message(test_user, 'Работать')
#sent = client.send_code_request(phone)