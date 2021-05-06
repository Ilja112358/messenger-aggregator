import grpc
import test_pb2
import test_pb2_grpc

channel = grpc.insecure_channel('84.252.137.106:6066')
stub = test_pb2_grpc.testStub(channel)
empty = test_pb2.Empty()
response = stub.get(empty)
print(response.data)

