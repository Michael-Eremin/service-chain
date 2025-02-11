package com.stockservice.infrastructure.grpc.process2;

import io.grpc.ManagedChannel;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientSolver {
    private final ManagedChannel channel;

    public GrpcClientSolver(ManagedChannel channel) {
        this.channel = channel;
    }


    public String sendStartProcessCommand(String command, Integer planId) {
        SolverServiceGrpc.SolverServiceBlockingStub stub = SolverServiceGrpc.newBlockingStub(channel);
        // Create request
        StartSolverRequest request = StartSolverRequest.newBuilder()
                .setCommand(command)
                .setPlanId(planId)
                .build();

        // cal server method
        StartSolverResponse response = stub.startSolver(request);

        System.out.println("Server response: " + response.getMessage());
        // Return response
        return response.getMessage();
    }

}
