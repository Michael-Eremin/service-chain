package com.stockservice.infrastructure.grpc.process2;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class GrpcClientSolver {
    private ManagedChannel channel;
    private SolverServiceGrpc.SolverServiceBlockingStub stub;
    @Value("${grpc.server.proc2.address}")
    private  String address2;
    @Value("${grpc.server.proc2.port}")
    private  int port2;

    public GrpcClientSolver() {}

    @PostConstruct
    public void init() {
        // Create channel
        System.out.println("address2 = " + address2);
        System.out.println("port2 = " + port2);
        this.channel = ManagedChannelBuilder.forAddress(address2, port2)
                .usePlaintext()
                .build();
        // Create stub for method call
        this.stub = SolverServiceGrpc.newBlockingStub(channel);
    }


    public String sendStartProcessCommand(String command, Integer planId) {
        // Create request
        StartSolverRequest request = StartSolverRequest.newBuilder()
                .setCommand(command)
                .setPlanId(planId)
                .build();

        // cal server method
        StartSolverResponse response = stub.startSolver(request);

        // Return response
        return response.getMessage();
    }

    @PreDestroy
    public void cleanup() throws InterruptedException {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

}
