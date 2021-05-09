from __future__ import print_function
import os.path
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from api.protobufs import gmail_pb2_grpc, gmail_pb2
from api.protobufs import common_pb2
from googleapiclient.http import BatchHttpRequest

import httplib2

# If modifying these scopes, delete the file token.json.
SCOPES = ['https://mail.google.com/']


class GmailApiServicer(gmail_pb2_grpc.GmailApiServicer):
    def auth(self, request, context):
        creds = None
        if os.path.exists('api/gmail_credentials/' + request.uid + '_token.json'):
            creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json',
                                                          SCOPES)
        if not creds or not creds.valid:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
            else:
                flow = InstalledAppFlow.from_client_secrets_file(
                    'api/gmail_credentials/' + request.uid + '_credentials.json', SCOPES)
                creds = flow.run_local_server(port=0)
            with open('api/gmail_credentials/' + request.uid + '_token.json', 'w') as token:
                token.write(creds.to_json())
        response = common_pb2.StatusMessage(status='OK AND')
        return response

    def get_dialogs(self, request, context):
        creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json', SCOPES)
        service = build('gmail', 'v1', credentials=creds)

        threads = service.users().threads().list(userId='me').execute().get('threads', [])

        names = []

        def list_names(request_id, response, exception):
            if exception is not None:
                print(response)
            else:
                print('Nu i pofig')

        batch = BatchHttpRequest()
        for thread in threads:
            batch.add(service.users().threads()
                      .get(userId='me', id=thread.get('id'),
                           format='metadata', metadataHeaders=['Subject']), list_names)
        http = httplib2.Http()
        batch.execute(http=http)
        print(names)

        dialogs = []
        for thread in threads:
            dialog = common_pb2.Dialog(message=thread.get('snippet'), thread_id=thread.get('id'))
            dialogs.append(dialog)

        response2 = common_pb2.Dialogs(dialog=dialogs)
        return response2

    def get_messages(self, request, context):
        creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json', SCOPES)
        service = build('gmail', 'v1', credentials=creds)

        thread = service.users().threads().get(userId='me', id=request.thread_id).execute().get('messages', [])
        messages = []
        for message in thread:
            snippet = message.get('snippet')
            date = int(message.get('internalDate'))
            name = ''
            for header in message.get('payload').get('headers'):
                if header.get('name') == 'From':
                    name = header.get('value')
            messages.append(common_pb2.Message(message=snippet, date=date, sender=name))
        print(messages)
        response = common_pb2.Messages(message=messages)
        return response

    def send_message(self, request, context):
        pass
