package com.stockservice.infrastructure.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;


@Service
public class GrpcClientServiceImpl {
    private final ManagedChannel channel;
    private final com.stockservice.infrastructure.grpc.ProcessServiceGrpc.ProcessServiceBlockingStub stub;

    public GrpcClientServiceImpl() {
        // create channel for server connect
        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        // create stub for methods call
        this.stub = com.stockservice.infrastructure.grpc.ProcessServiceGrpc.newBlockingStub(channel);
    }


    public String sendStartProcessCommand(String command, Integer planId) {
        // Create request
        com.stockservice.infrastructure.grpc.StartProcessRequest request = com.stockservice.infrastructure.grpc.StartProcessRequest.newBuilder()
                .setCommand(command)
                .setPlanId(planId)
                .build();

        // cal server method
        com.stockservice.infrastructure.grpc.StartProcessResponse response = stub.startProcess(request);

        // Return response
        return response.getMessage();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
    }
}
