package com.stockservice.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/check")
@Tag(name = "CHECK API", description = "Endpoints for interacting with the Stock service")
public class CheckController {

    private final GrpcClient grpcClient;

    public CheckController(GrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }

    @GetMapping("/start-process")
    public String startProcess(@RequestParam String command) {
        return grpcClient.sendStartProcessCommand(command);
    }
}
