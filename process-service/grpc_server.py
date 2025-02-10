import threading
import grpc
from concurrent import futures
import logging
from subprocess import Popen, PIPE, DEVNULL
import process2_pb2
import process2_pb2_grpc
import queue
import signal
import sys


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Semaphore for maximum of 3 processes at a time
semaphore = threading.Semaphore(5)

# Queue for task with max value
task_queue = queue.Queue(maxsize=50)

# Flag for correct shutdown
shutdown_event = threading.Event()

# SolverService implementation
class SolverServiceServicer(process2_pb2_grpc.SolverServiceServicer):
    def StartSolver(self, request, context):
        if shutdown_event.is_set():
            # Если сервер завершается, отклоняем новые запросы
            context.set_code(grpc.StatusCode.UNAVAILABLE)
            context.set_details("Server is shutting down")
            return process2_pb2.StartSolverResponse()

        command = request.command
        plan_id = request.planId

        # Create task
        task = {"command": command, "plan_id": plan_id}

        try:
            #  Put task to queue (except if queue is full)
            task_queue.put(task, timeout=5)  # Timeout to stop waiting indefinitely
            logger.info(f"Process UPDATE to --> PENDING planId={plan_id}")
        except queue.Full:
            logger.error("Task queue is full. Rejecting new tasks.")
            context.set_code(grpc.StatusCode.RESOURCE_EXHAUSTED)
            context.set_details("Task queue is full. Please try again later.")
            return process2_pb2.StartSolverResponse()

        # Return message to client
        return process2_pb2.StartSolverResponse(
            message=f"Task with planId={plan_id} accepted as PENDING."
        )

def process_tasks():
    """Фоновая функция для обработки задач из очереди"""
    while not shutdown_event.is_set() or not task_queue.empty():
        try:
            # Get task from queue (block call)
            task = task_queue.get(timeout=1)  # Timeout for checking on shutdown_event
            plan_id = task.get("plan_id")

            # Trying to take over permission from a semaphore
            if shutdown_event.is_set():
                logger.info("Shutdown in progress. Skipping task processing.")
                task_queue.task_done()
                continue

            # Trying to take over permission from a semaphore
            semaphore.acquire()
            logger.info("Semaphore acquired. Starting process...")

            # Command to run process_chain.py
            cmd = f"python3 process_chain.py --plan {plan_id}"

            # Run process
            process = Popen(cmd, stdout=PIPE, stderr=PIPE, shell=True)
            logger.info(f"Process UPDATE to --> RUNNING pid: {process.pid} planId={plan_id}")
            logger.info(f"Command started: {cmd}")


            def release_semaphore(process):
                """
                Tracks process completion
                :param process:
                """
                try:
                    stdout, stderr = process.communicate()
                    exit_code = process.returncode
                    semaphore.release()  # semaphore release
                    if exit_code == 0:
                        logger.info(f"Process PID {process.pid} finished successfully.")
                    else:
                        logger.error(f"Process PID {process.pid} failed with exit code: {exit_code}, stderr: {stderr.decode('utf-8')}")
                finally:
                    # Notify the queue that the task has been processed
                    task_queue.task_done()

            # Run process termination tracking in a separate thread
            threading.Thread(target=lambda: release_semaphore(process), daemon=True).start()

        except queue.Empty:
            # Continue the cycle if the queue is empty
            continue
        except Exception as e:
            logger.error(f"Error processing task: {e}")
            semaphore.release()  # Check semaphore and queue release
            task_queue.task_done()  # Check semaphore and queue release

def correct_shutdown(signum, frame):
    """Handles signals for correct shutdown"""
    logger.info("Received shutdown signal. Initiating correct shutdown...")
    shutdown_event.set()

    # Wait all tasks end in queue
    logger.info("Waiting for all tasks to complete...")
    task_queue.join()

    logger.info("All tasks completed. Exiting.")
    sys.exit(0)

def serve():
    """Run server"""
    # Registration of signal handlers
    signal.signal(signal.SIGTERM, correct_shutdown)
    signal.signal(signal.SIGINT, correct_shutdown)

    # Starting task handling in a background thread
    threading.Thread(target=process_tasks, daemon=True).start()

    # Create gRPC-server
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    process2_pb2_grpc.add_SolverServiceServicer_to_server(SolverServiceServicer(), server)

    # Set port
    server.add_insecure_port('0.0.0.0:50051')
    logger.info("gRPC-SolverService server is running and listening on the port 50051")

    # Start server
    server.start()
    server.wait_for_termination()

if __name__ == '__main__':
    serve()

