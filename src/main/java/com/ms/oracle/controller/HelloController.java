package com.ms.oracle.controller;

import com.ms.oracle.domain.Hello;
import com.ms.oracle.repository.HelloRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HelloController {

    private final HelloRepository helloRepository;

    public HelloController(HelloRepository helloRepository) {
        this.helloRepository = helloRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Hello>> all() {
        return ResponseEntity.ok(
            helloRepository.findAll()
        );
    }

    @PostMapping("/")
    public ResponseEntity<Hello> add(@RequestBody Hello hello) {
        return ResponseEntity.ok(
          helloRepository.save(Hello.builder().greeting(hello.getGreeting()).build())
        );
    }

}
