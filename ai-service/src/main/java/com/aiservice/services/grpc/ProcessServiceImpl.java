package com.aiservice.services.grpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class ProcessServiceImpl  extends ProcessServiceGrpc.ProcessServiceImplBase {

    @Override
    public void startProcess(StartProcessRequest request, StreamObserver<StartProcessResponse> responseObserver) {
        String command = request.getCommand();
        int planId = request.getPlanId();

        String responseMessage = String.format(
                "Command '%s' received for planId %d and processed.",
                command, planId
        );

        StartProcessResponse response = StartProcessResponse.newBuilder()
                .setMessage(responseMessage)
                .build();

        // send to client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}