from telethon import TelegramClient, events, sync
from telethon.tl.types import InputMessagesFilterPhotos
from time import sleep


#user authorizathion
api_id = <your_api_id>
api_hash = '<your_api_hash>'
phone1 = '<your_phone>'
client = TelegramClient('sessions/andrew', api_id, api_hash)
code = 1234
client.sign_in(phone, code)
#client.start(phone)
#dialogs = client.get_dialogs()
#print(dialogs[3])
chat = 'test'
i = 0
'''while i < 20:
    client.send_message('@durmankoo', 'ПРОГУЛОЧКУ БЫ')
    sleep(2)
    i += 1'''
#sent = client.send_code_request(phone)
