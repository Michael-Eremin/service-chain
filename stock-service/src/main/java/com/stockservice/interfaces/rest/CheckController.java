package com.stockservice.interfaces.rest;


import com.stockservice.services.grpc.GrpcClientServiceImpl;
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
    public CheckController(
            @Autowired GrpcClientServiceImpl grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @GetMapping("/start-process")
    public String startProcess(@RequestParam String command) {
        return grpcClientService.sendStartProcessCommand(command);
    }
}
