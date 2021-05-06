from telethon import TelegramClient, events, sync
from telethon.tl.types import InputMessagesFilterPhotos
from time import sleep


#user authorizathion
phone = <your phone>
api_id = <your_id>
api_hash = <your hash>
client = TelegramClient('sessions/Consumer', api_id, api_hash)
client.start()
dialogs = client.get_dialogs()
#print(dialogs[12])
chat = 'test'
while 1:
    client.send_message(595779751, 'ПРОГУЛОЧКУ БЫ')
    sleep(2)
#sent = client.send_code_request(phone)
