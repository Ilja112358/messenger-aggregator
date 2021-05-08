from concurrent import futures
import grpc
from api.telegram import TgApiServicer
from api.protobufs import tg_pb2_grpc


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=4))
    tg_pb2_grpc.add_TgApiServicer_to_server(TgApiServicer(), server)
    print('Starting server on port 6066.')
    server.add_insecure_port('[::]:6066')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    serve()
