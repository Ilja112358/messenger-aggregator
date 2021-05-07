import time
from concurrent import futures
import grpc
# import test_pb2
# import test_pb2_grpc
from api.telegram import TgApiServicer
from api.protobufs import tg_pb2_grpc

'''
class TestServicer(test_pb2_grpc.testServicer):
    def get(self, request):
        response = test_pb2.Text()
        response.data = "Test 1"
        return response
'''

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=4))
    tg_pb2_grpc.add_TgApiServicer_to_server(TgApiServicer, server)
    # test_pb2_grpc.add_testServicer_to_server(TestServicer, server)
    print('Starting server on port 6066.')
    server.add_insecure_port('[::]:6066')
    server.start()
    try:
        while True:
            time.sleep(3600)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    serve()
