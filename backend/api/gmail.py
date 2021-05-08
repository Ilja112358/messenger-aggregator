from __future__ import print_function
import os.path
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from api.protobufs import gmail_pb2_grpc
from api.protobufs import gmail_pb2
from api.protobufs import common_pb2

# If modifying these scopes, delete the file token.json.
SCOPES = ['https://mail.google.com/']


class GmailApiServicer(gmail_pb2_grpc.GmailApiServicer):
    def auth(self, request, context):
        creds = None
        if os.path.exists('api/gmail_credentials/' + request.uid + '_token.json'):
            creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json', SCOPES)
        if not creds or not creds.valid:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
            else:
                flow = InstalledAppFlow.from_client_secrets_file('api/gmail_credentials/' + request.uid + '_credentials.json', SCOPES)
                creds = flow.run_local_server(port=0)
            with open('api/gmail_credentials/' + request.uid + '_token.json', 'w') as token:
                token.write(creds.to_json())
        response = common_pb2.StatusMessage(status='OK AND')
        return response

    def get_dialogs(self, request, context):
        creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json', SCOPES)
        service = build('gmail', 'v1', credentials=creds)

        threads = service.users().threads().list(userId='me').execute().get('threads', [])
        dialogs = [common_pb2.Dialog(message=thread.get('snippet'), thread_id=thread.get('id')) for thread in threads]
        response = common_pb2.Dialogs(dialog=dialogs)
        return response

    def get_messages(self, request, context):
        creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json', SCOPES)
        service = build('gmail', 'v1', credentials=creds)

        thread = service.users().threads().get(userId='me', id=request.thread_id).execute().get('messages', '')
        messages = [common_pb2.Message(message=message.get('snippet'), date=message.get('internalDate')) for message in thread]
        response = common_pb2.Messages(message=messages)
        return response

    def send_message(self, request, context):
        pass
