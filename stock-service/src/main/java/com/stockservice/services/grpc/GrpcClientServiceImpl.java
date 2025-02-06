package com.stockservice.services.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;


@Service
public class GrpcClientServiceImpl {
    private final ManagedChannel channel;
    private final com.stockservice.services.grpc.ProcessServiceGrpc.ProcessServiceBlockingStub stub;

    public GrpcClientServiceImpl() {
        // create channel for server connect
        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        // create stub for methods call
        this.stub = com.stockservice.services.grpc.ProcessServiceGrpc.newBlockingStub(channel);
    }


    public String sendStartProcessCommand(String command) {
        // Create request
        com.stockservice.services.grpc.StartProcessRequest request = com.stockservice.services.grpc.StartProcessRequest.newBuilder()
                .setCommand(command)
                .build();

        // cal server method
        com.stockservice.services.grpc.StartProcessResponse response = stub.startProcess(request);

        // Return response
        return response.getMessage();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
    }
}
