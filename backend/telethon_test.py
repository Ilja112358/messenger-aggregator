from telethon import TelegramClient, events, sync, functions, utils
from telethon.tl.types import InputMessagesFilterPhotos
from time import sleep


#user authorizathion
phone = '+79213010190'
api_id = 4139893
api_hash = 'ece013c7c8869d3a584bb2cae5ae3fc7'
client = TelegramClient('sessions/Andrew', api_id, api_hash)
code = 1234
#client.sign_in(phone, code)
client.start()
#dialogs = client.get_messages(423865152, 10)
dialogs = client.get_messages(-1001389304944, 10)
temp_dialogs = client.get_dialogs()
'''for dialog in temp_dialogs:
    print(dialog.name + ' ' + str(client.get_peer_id(dialog)))'''
print(client.get_entity(-1001389304944))
#print(type(dialogs))
#print(client.get_entity(423865152))

#print(utils.get_input_location(dialogs[0]))
#print(dialogs[0])
test_str =str
#print(test_str)
#dialogs[0].download_media(dialogs[0], test_str)
#print(dialogs[0].download_media(test_str))
#print(utils.get_attributes(dialogs[0].media.document.id))


#print(len(temp_dialogs))
'''for dialog in temp_dialogs:
    #print(client.get_peer_id(dialog))
    peer_id = client.get_peer_id(dialog)
    if peer_id > 0:
        if peer_id == 423865152:
            print(dialog.message.peer_id.user_id, ' ', dialog.name) 

print(client.get_entity(423865152).first_name) '''

'''dialog_id = -595779751
temp_messages = client.get_messages(dialog_id, 200)
if dialog_id > 0:
    for dialog in temp_dialogs:
        peer_id = client.get_peer_id(dialog)
        if peer_id == dialog_id:
            name = dialog.name
for temp_message in temp_messages:
    if temp_message.out == True:
        sender = 'me'
    else:
        if dialog_id > 0:
            sender = name
        else:
            print(client.get_entity(temp_message.from_id.user_id))
            sender = client.get_entity(temp_message.from_id.user_id).first_name + ' ' + client.get_entity(
                temp_message.from_id.user_id).last_name
    print(sender)'''

#print(client.get_entity(423865152).first_name + ' ' +client.get_entity(423865152).last_name)

#print(dialogs[0].message)
#dialogs = client.get_dialogs()
#print(dialogs[1].message.from_id.user_id)
i = 0
#client.send_message(-465125280, 'test')
#print(dialogs[i].name)
#print(dialogs[i])



#print(dialogs[i].message.peer_id.channel_id)
#print(type(client.get_entity(dialogs[i])), dialogs[i].name)
#print(dialogs[i].id, '', dialogs[i].name)

#for dialog in dialogs:
 #   i += 1
  #  print(dialog)
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
