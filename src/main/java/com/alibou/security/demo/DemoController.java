package com.alibou.security.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/demo-controller")
public class DemoController {

    @GetMapping
    public ResponseEntity<String> sayhello() {
        return ResponseEntity.ok("Hello World!");
    }
}
