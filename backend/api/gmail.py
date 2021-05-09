from __future__ import print_function
import os.path
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from api.protobufs import gmail_pb2_grpc, gmail_pb2
from api.protobufs import common_pb2
import redis
import base64

# If modifying these scopes, delete the file token.json.
SCOPES = ['https://mail.google.com/']


def list_names(request_id, response, exception):
    if exception is not None:
        print(response)
    else:
        print('Nu i pofig')


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
        r = redis.Redis(host='localhost', port=6379, db=0)
        creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json', SCOPES)
        service = build('gmail', 'v1', credentials=creds)

        threads = service.users().threads().list(userId='me').execute().get('threads', [])

        dialogs = []
        for thread in threads:
            name = r.get(thread.get('id'))
            if name is None:
                name = service.users().threads().get(userId='me', id=thread.get('id'),
                                                     format='metadata', metadataHeaders=['Subject']).execute().get(
                    'messages')[0].get('payload').get('headers')[0].get('value')
                r.set(thread.get('id'), name)
            dialog = common_pb2.Dialog(message=thread.get('snippet'), thread_id=thread.get('id'), name=name)
            dialogs.append(dialog)

        response = common_pb2.Dialogs(dialog=dialogs)
        return response

    def get_messages(self, request, context):
        creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json', SCOPES)
        service = build('gmail', 'v1', credentials=creds)

        me_email = service.users().getProfile(userId='me').execute().get('emailAddress')

        thread = service.users().threads().get(userId='me', id=request.thread_id).execute().get('messages', [])
        messages = []
        for message in thread:
            snippet = message.get('snippet')
            date = int(message.get('internalDate'))
            name = ''
            for header in message.get('payload').get('headers'):
                if header.get('name') == 'From':
                    name = 'me' if me_email in header.get('value') else header.get('value')
            messages.append(common_pb2.Message(message=snippet, date=date, sender=name))
        print(messages)
        response = common_pb2.Messages(message=messages)
        return response

    def send_message(self, request, context):
        creds = Credentials.from_authorized_user_file('api/gmail_credentials/' + request.uid + '_token.json', SCOPES)
        service = build('gmail', 'v1', credentials=creds)

        me_email = service.users().getProfile(userId='me').execute().get('emailAddress')

        headers = service.users().threads() \
            .get(userId='me', id=request.thread_id, format='metadata', metadataHeaders=['From', 'To']).execute() \
            .get('messages')[0].get('payload').get('headers')
        to = headers[0].get('value') if me_email in headers[1].get('value') else headers[1].get('value')
        smtp = 'To: ' + to + '\nSubject: ' + request.subject + '\n\n' + request.message
        raw = base64.b64encode(bytes(smtp, encoding='utf8')).decode('utf-8')
        service.users().messages().send(userId='me', body={'raw': raw, 'threadId': request.thread_id}).execute()
        return common_pb2.StatusMessage(status='OK AND')
