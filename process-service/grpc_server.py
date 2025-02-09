import grpc
from concurrent import futures
import logging
from subprocess import Popen, PIPE, DEVNULL
import process2_pb2  # Изменено: Импортируем сгенерированный модуль process2_pb2
import process2_pb2_grpc  # Изменено: Импортируем сгенерированный модуль process2_pb2_grpc

# Настройка логирования
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


# Реализация сервиса SolverService
class SolverServiceServicer(process2_pb2_grpc.SolverServiceServicer):  # Изменено: Наследуемся от SolverServiceServicer
    def StartSolver(self, request, context):  # Изменено: Метод StartSolver вместо StartProcess
        command = request.command  # Добавлено: Получаем значение поля command
        plan_id = request.planId
        logger.info(f"Получен запрос на запуск solver с command: {command}, planId: {plan_id}")

        # command for call process_chain.py
        cmd = f"python3 process_chain.py --plan {plan_id}"

        try:
            # run subprocess
            process = Popen(cmd, stdout=DEVNULL, stderr=DEVNULL, shell=True)

            logger.info(f"Command started: {cmd}")
            logger.info(f"process.pid {process.pid}")
            return process2_pb2.StartSolverResponse(
                    message=f"Solver с command={command},"
                            f" planId={plan_id},"
                            f" process.pid={process.pid} is started")
        except Exception as e:
            logger.error(f"Error command: {cmd}\n{e}")
            return process2_pb2.StartSolverResponse(message=f"Error run process: {str(e)}")



# Функция для запуска сервера
def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))

    # Регистрируем сервис SolverService
    process2_pb2_grpc.add_SolverServiceServicer_to_server(SolverServiceServicer(),
                                                          server)  # Изменено: Регистрируем SolverService

    # Указываем порт для прослушивания
    server.add_insecure_port('0.0.0.0:50051')

    # Логирование старта сервера
    logger.info("gRPC-сервер SolverService запущен и слушает порт 50051")

    # Запускаем сервер
    server.start()
    server.wait_for_termination()


# Точка входа
if __name__ == '__main__':
    serve()