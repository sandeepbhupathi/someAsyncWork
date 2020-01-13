package com.example.demo.controller;

import com.example.demo.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private SampleService sampleService;
    @RequestMapping(path = "/hi")
    public String sayHi(){
        return "hello";
    }

    @RequestMapping("/batchStatus")
    public String batchStatus(){
        return sampleService.getBatchStat();
    }

    @RequestMapping("/invokeBatch")
    public String invokeBatch(){
        sampleService.invokeAsync();
        return "RUNNING";
    }

    @RequestMapping("/invokeBatch2")
    public String invokeBatch2(){
        sampleService.invokeAsync2();
        return "RUNNING";
    }
}
