from telethon import TelegramClient, events, sync, functions
from telethon.tl.types import InputMessagesFilterPhotos
from time import sleep


#user authorizathion
phone = '+79992083343'
api_id = 4139893
api_hash = 'ece013c7c8869d3a584bb2cae5ae3fc7'
client = TelegramClient('sessions/Consumer', api_id, api_hash)
code = 1234
#client.sign_in(phone, code)
client.start()
dialogs = client.get_messages(642388933, 2)
dialogs = client.get_dialogs()
#print(dialogs[1].message.from_id.user_id)
i = 0
print(dialogs[i].name)
print(dialogs[i].message.peer_id.channel_id)
#print(type(client.get_entity(dialogs[i])), dialogs[i].name)
#print(dialogs[i].id, '', dialogs[i].name)

#for dialog in dialogs:
#    print(dialog.name, ' ', dialog.message.from_id.user_id)
#print(dialogs)
#result = client(functions.messages.GetPeerDialogsRequest(peers=['username']))
#print(result.dialogs[0].unread_count)


#chat = 'test'
#i = 0
'''while i < 20:
    client.send_message('@durmankoo', 'message')
    sleep(2)
    i += 1'''
#sent = client.send_code_request(phone)
