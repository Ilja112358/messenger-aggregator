from telethon import TelegramClient, events, sync, functions
from telethon.tl.types import InputMessagesFilterPhotos
from time import sleep


#user authorizathion
phone = <your_data>
api_id = <your_data>
api_hash = <your_data>
client = TelegramClient('sessions/Consumer', api_id, api_hash)
code = 1234
#client.sign_in(phone, code)
client.start()
dialogs = client.get_messages(642388933, 2)
dialogs = client.get_dialogs()
#print(dialogs[1].message.from_id.user_id)
i = 0
client.send_message(-465125280, 'test')
#print(dialogs[i].name)
#print(dialogs[i])



#print(dialogs[i].message.peer_id.channel_id)
#print(type(client.get_entity(dialogs[i])), dialogs[i].name)
#print(dialogs[i].id, '', dialogs[i].name)

for dialog in dialogs:
    i += 1
    print(client.get_peer_id(dialog), ' ', dialog.name)
    '''   print(i)
    dialog_id = 0
    print(dialog.name)
    print(dialog)
    if str(type(client.get_entity(dialog))) == "<class 'telethon.tl.types.User'>":
        dialog_id = int(dialog.message.from_id.user_id)
    elif str(type(client.get_entity(dialog))) == "<class 'telethon.tl.types.Channel'>":
        dialog_id = int(dialog.message.peer_id.channel_id)
    print(dialog_id)'''
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
