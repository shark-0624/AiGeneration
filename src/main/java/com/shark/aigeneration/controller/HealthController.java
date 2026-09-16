package com.shark.aigeneration.controller;

import com.shark.aigeneration.common.BaseResponse;
import com.shark.aigeneration.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("嘿嘿");
    }
}
