from concurrent import futures
import grpc
from api.telegram import TgApiServicer
from api.gmail import GmailApiServicer
from api.protobufs import tg_pb2_grpc
from api.protobufs import gmail_pb2_grpc

port = 6066


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=4))
    tg_pb2_grpc.add_TgApiServicer_to_server(TgApiServicer(), server)
    gmail_pb2_grpc.add_GmailApiServicer_to_server(GmailApiServicer(), server)
    print('Starting server on port', port)
    server.add_insecure_port('[::]:' + str(port))
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    serve()
