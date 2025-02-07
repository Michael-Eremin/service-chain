package com.stockservice.interfaces.rest;


import com.stockservice.infrastructure.grpc.process.GrpcClientServiceImpl;
import com.stockservice.infrastructure.grpc.process2.GrpcClientSolver;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/check")
@Tag(name = "CHECK API", description = "Endpoints for interacting with the Stock service")
public class CheckController {

    private final GrpcClientServiceImpl grpcClientService;
    private final GrpcClientSolver grpcClientSolver;
    public CheckController(
            @Autowired GrpcClientServiceImpl grpcClientService,
            @Autowired GrpcClientSolver grpcClientSolver
    ) {
        this.grpcClientService = grpcClientService;
        this.grpcClientSolver = grpcClientSolver;
    }

    @GetMapping("/start-process")
    public String startProcess(
            @RequestParam String command,
            @RequestParam Integer planId
    ) {
        return grpcClientService.sendStartProcessCommand(command, planId);
    }

    @GetMapping("/start-process2")
    public String startProcess2(
            @RequestParam String command,
            @RequestParam Integer planId
    ) {
        return grpcClientSolver.sendStartProcessCommand(command, planId);
    }
}
