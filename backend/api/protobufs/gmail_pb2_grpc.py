# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
"""Client and server classes corresponding to protobuf-defined services."""
import grpc

from api.protobufs import common_pb2 as common__pb2
from api.protobufs import gmail_pb2 as gmail__pb2


class GmailApiStub(object):
    """Missing associated documentation comment in .proto file."""

    def __init__(self, channel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """
        self.auth = channel.unary_unary(
                '/GmailApi/auth',
                request_serializer=gmail__pb2.GmailAuthRequest.SerializeToString,
                response_deserializer=common__pb2.AuthResponse.FromString,
                )
        self.get_dialogs = channel.unary_unary(
                '/GmailApi/get_dialogs',
                request_serializer=common__pb2.User.SerializeToString,
                response_deserializer=common__pb2.Dialogs.FromString,
                )
        self.get_messages = channel.unary_unary(
                '/GmailApi/get_messages',
                request_serializer=common__pb2.Text.SerializeToString,
                response_deserializer=common__pb2.Text.FromString,
                )
        self.send_message = channel.unary_unary(
                '/GmailApi/send_message',
                request_serializer=common__pb2.Send.SerializeToString,
                response_deserializer=common__pb2.StatusMessage.FromString,
                )


class GmailApiServicer(object):
    """Missing associated documentation comment in .proto file."""

    def auth(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def get_dialogs(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def get_messages(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')

    def send_message(self, request, context):
        """Missing associated documentation comment in .proto file."""
        context.set_code(grpc.StatusCode.UNIMPLEMENTED)
        context.set_details('Method not implemented!')
        raise NotImplementedError('Method not implemented!')


def add_GmailApiServicer_to_server(servicer, server):
    rpc_method_handlers = {
            'auth': grpc.unary_unary_rpc_method_handler(
                    servicer.auth,
                    request_deserializer=gmail__pb2.GmailAuthRequest.FromString,
                    response_serializer=common__pb2.AuthResponse.SerializeToString,
            ),
            'get_dialogs': grpc.unary_unary_rpc_method_handler(
                    servicer.get_dialogs,
                    request_deserializer=common__pb2.User.FromString,
                    response_serializer=common__pb2.Dialogs.SerializeToString,
            ),
            'get_messages': grpc.unary_unary_rpc_method_handler(
                    servicer.get_messages,
                    request_deserializer=common__pb2.Text.FromString,
                    response_serializer=common__pb2.Text.SerializeToString,
            ),
            'send_message': grpc.unary_unary_rpc_method_handler(
                    servicer.send_message,
                    request_deserializer=common__pb2.Send.FromString,
                    response_serializer=common__pb2.StatusMessage.SerializeToString,
            ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
            'GmailApi', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


 # This class is part of an EXPERIMENTAL API.
class GmailApi(object):
    """Missing associated documentation comment in .proto file."""

    @staticmethod
    def auth(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/GmailApi/auth',
            gmail__pb2.GmailAuthRequest.SerializeToString,
            common__pb2.AuthResponse.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def get_dialogs(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/GmailApi/get_dialogs',
            common__pb2.User.SerializeToString,
            common__pb2.Dialogs.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def get_messages(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/GmailApi/get_messages',
            common__pb2.Text.SerializeToString,
            common__pb2.Text.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)

    @staticmethod
    def send_message(request,
            target,
            options=(),
            channel_credentials=None,
            call_credentials=None,
            insecure=False,
            compression=None,
            wait_for_ready=None,
            timeout=None,
            metadata=None):
        return grpc.experimental.unary_unary(request, target, '/GmailApi/send_message',
            common__pb2.Send.SerializeToString,
            common__pb2.StatusMessage.FromString,
            options, channel_credentials,
            insecure, call_credentials, compression, wait_for_ready, timeout, metadata)
