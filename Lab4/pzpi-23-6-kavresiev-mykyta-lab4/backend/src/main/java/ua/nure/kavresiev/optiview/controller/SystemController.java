package ua.nure.kavresiev.optiview.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class SystemController {

    @GetMapping("/api/system/instance")
    public Map<String, Object> getInstanceInfo() {
        Map<String, Object> response = new HashMap<>();

        String hostname = "unknown";

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {
        }

        response.put("status", "OK");
        response.put("service", "Oftalmika Backend");
        response.put("currentBackendInstance", hostname);
        response.put("message", "This request was processed by one backend container behind Nginx Load Balancer");
        response.put("time", LocalDateTime.now().toString());

        return response;
    }
}