package com.aiservice.services.grpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.aiservice.services.grpc.ProcessProto;
import com.aiservice.services.grpc.ProcessServiceGrpc;

@GrpcService
public class ProcessServiceImpl  extends ProcessServiceGrpc.ProcessServiceImplBase {



    @Override
    public void startProcess(StartProcessRequest request, StreamObserver<StartProcessResponse> responseObserver) {
        String command = request.getCommand();
        int planId = request.getPlanId();

        // Логика обработки команды
        String responseMessage = String.format(
                "Command '%s' received for planId %d and processed.",
                command, planId
        );

        // Формируем ответ
        StartProcessResponse response = StartProcessResponse.newBuilder()
                .setMessage(responseMessage)
                .build();

        // Отправляем ответ клиенту
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}