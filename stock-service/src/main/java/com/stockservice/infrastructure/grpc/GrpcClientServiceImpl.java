package com.stockservice.infrastructure.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class GrpcClientServiceImpl {
    private ManagedChannel channel;
    private ProcessServiceGrpc.ProcessServiceBlockingStub stub;
    @Value("${grpc.server.address}")
    private  String address;
    @Value("${grpc.server.port}")
    private  int port;


    public GrpcClientServiceImpl() {}


    @PostConstruct
    public void init() {
        // Create channel
        this.channel = ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();
        // Create stub for method call
        this.stub = ProcessServiceGrpc.newBlockingStub(channel);
    }


    public String sendStartProcessCommand(String command, Integer planId) {
        // Create request
        StartProcessRequest request = StartProcessRequest.newBuilder()
                .setCommand(command)
                .setPlanId(planId)
                .build();

        // cal server method
        StartProcessResponse response = stub.startProcess(request);

        // Return response
        return response.getMessage();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void cleanup() throws InterruptedException {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
