from flask import Flask, request
from telethon import TelegramClient, events, sync
from telethon.tl.types import InputMessagesFilterPhotos


def out(username):
    api_id = 4139893
    api_hash = 'ece013c7c8869d3a584bb2cae5ae3fc7'

app = Flask(__name__)
@app.route('/api/tg/sendMessage', method='POST')
def sendMessage():
    if request.form['type'] == 'chat':
        client_session = 'sessions/' + request.form['username']



