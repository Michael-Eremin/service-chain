package com.stockservice.infrastructure.grpc.process2;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientConfig {
    @Value("${grpc.server.proc2.host}")
    private String host;

    @Value("${grpc.server.proc2.port}")
    private int port;

    private ManagedChannel channel;

    @Bean
    public ManagedChannel managedChannel() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        return channel;
    }

    @PreDestroy
    public void cleanup() throws InterruptedException {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

}
