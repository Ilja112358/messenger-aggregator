from telethon import TelegramClient, events, sync
from telethon.tl.types import InputMessagesFilterPhotos
from time import sleep


#user authorizathion
phone = '+79992083343'
api_id = 4139893
api_hash = 'ece013c7c8869d3a584bb2cae5ae3fc7'
client = TelegramClient('sessions/Consumer3', api_id, api_hash)
code = 1234
#client.sign_in(phone, code)
client.start()
dialogs = client.get_dialogs()
print(len(dialogs))
#print(dialogs)





#chat = 'test'
#i = 0
'''while i < 20:
    client.send_message('@durmankoo', 'ПРОГУЛОЧКУ БЫ')
    sleep(2)
    i += 1'''
#sent = client.send_code_request(phone)
