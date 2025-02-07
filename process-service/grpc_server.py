import grpc
from concurrent import futures
import logging
import subprocess
import process_pb2
import process_pb2_grpc


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class ProcessServiceServicer(process_pb2_grpc.ProcessServiceServicer):
    def StartProcess(self, request, context):

        plan_id = request.planId
        logger.info(f"Request to start process received с planId: {plan_id}")

        # command for call process_chain.py
        cmd = f"python3 process_chain.py --plan {plan_id}"

        try:
            # run subprocess
            process = subprocess.Popen(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, shell=True)
            logger.info(f"Command started: {cmd}")
            return process_pb2.StartProcessResponse(message=f"Process with planId {plan_id} started. PID={process.pid}")
        except Exception as e:
            logger.error(f"Error command: {cmd}\n{e}")
            return process_pb2.StartProcessResponse(message=f"Error run process: {str(e)}")

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    process_pb2_grpc.add_ProcessServiceServicer_to_server(ProcessServiceServicer(), server)
    server.add_insecure_port('[::]:50051')
    logger.info("gRPC-server is running and listening port 50051")
    server.start()
    server.wait_for_termination()

if __name__ == '__main__':
    serve()