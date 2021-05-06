import grpc
import datahash_pb2
import datahash_pb2_grpc

channel = grpc.insecure_channel('84.252.137.106:6066')
stub = datahash_pb2_grpc.DataHashStub(channel)
text = 'Scio me nihil scire'
to_md5 = datahash_pb2.Text(data=text)
response = stub.hash_md5(to_md5)
print(response.data)
to_sha256 = datahash_pb2.Text(data=text)
response = stub.hash_sha256(to_sha256)
print(response.data)

