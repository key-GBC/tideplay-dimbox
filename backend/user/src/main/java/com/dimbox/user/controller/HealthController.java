package com.dimbox.user.controller;

import com.dimbox.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class HealthController {

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("ok");
    }
}
