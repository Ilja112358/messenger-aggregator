from __future__ import print_function
import os.path
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from api.protobufs import gmail_pb2_grpc
from api.protobufs import gmail_pb2

# If modifying these scopes, delete the file token.json.
SCOPES = ['https://mail.google.com/']


class GmailApiServicer(gmail_pb2_grpc.GmailApiServicer):
    def auth(self, request, context):
        creds = None
        if os.path.exists(request.uid + '_token.json'):
            creds = Credentials.from_authorized_user_file(request.uid + '_token.json', SCOPES)
        if not creds or not creds.valid:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
            else:
                flow = InstalledAppFlow.from_client_secrets_file(request.uid + '_credentials.json', SCOPES)
                creds = flow.run_local_server(port=0)
            with open(request.uid + '_token.json', 'w') as token:
                token.write(creds.to_json())

    def get_dialogs(self, request, context):
        creds = Credentials.from_authorized_user_file(request.uid + 'token.json', SCOPES)
        service = build('gmail', 'v1', credentials=creds)

        results = service.users().threads().list(userId='me').execute()
        threads = results.get('threads', [])
        print(threads)

    def get_messages(self, request, context):
        pass

    def send_message(self, request, context):
        pass
