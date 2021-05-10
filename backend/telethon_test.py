from telethon import TelegramClient, events, sync, functions, utils
from telethon.tl.types import InputMessagesFilterPhotos
import os
from time import sleep


#user authorizathion
phone = '<your_phone>'
api_id = <your_api_id>
api_hash = '<your_api_hash>'
client = TelegramClient('sessions/Andrew', api_id, api_hash)
#client.sign_in(phone, code)
client.start()
#dialogs = client.get_messages(423865152, 10)


dialogs = client.get_messages(-1001173245559, 200)
temp_dialogs = client.get_dialogs()
filename = "test_image"
'''if client.download_media(dialogs[0], 'images/testphoto') == None:
    pass'''
print(str(type(client.get_entity(-595779751))))
i = 0
'''for temp_dialog in temp_dialogs:
    temp_dialog_id = client.get_peer_id(temp_dialog)
    if client.download_profile_photo(temp_dialog_id, 'avatars/' + str(temp_dialog_id) + '.jpg') == None:
        avatar_url = ''
    else:
        avatar_url = 'http://yout_server' + str(temp_dialog_id) + 'jpg'
    print(avatar_url)'''
'''temp_dialog_id = -595779751
if os.path.exists('avatars/' + str(temp_dialog_id) + '.jpg'):
   print('exists')

if client.download_profile_photo(temp_dialog_id, 'avatars/' + str(temp_dialog_id) + '.jpg') == None:
    avatar_url = ''
else:
    avatar_url = 'http://your_server' + str(temp_dialog_id) + 'jpg'''''

'''for dialog in dialogs:
    if i < 20:
        client.download_profile_photo(client.get_entity(dialog.from_id.user_id), 'avatars/')
        i += 1'''




#print(dialogs)
'''for dialog in temp_dialogs:
    print(dialog.name + ' ' + str(client.get_peer_id(dialog)))'''
#print(client.get_entity(-1001389304944))
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
