from telethon import TelegramClient, events, sync
from telethon.tl.types import InputMessagesFilterPhotos
from time import sleep


#user authorizathion
phone = '<ypur_phone>'
api_id = <your_api_id>
api_hash = '<your_api_hash>'
client = TelegramClient('sessions/uid', api_id, api_hash)
code = 1234
#client.sign_in(phone, code)
client.start()
dialogs = client.get_dialogs()
print(len(dialogs))
#print(dialogs)





#chat = 'test'
#i = 0
'''while i < 20:
    client.send_message('@durmankoo', 'message')
    sleep(2)
    i += 1'''
#sent = client.send_code_request(phone)
