package com.example.demonew1.controller;

import com.example.demonew1.domain.EventDetails;
import com.example.demonew1.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/batch")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @RequestMapping("/start")
    public String start(){
        return "RUNNING";
    }

    @RequestMapping("/stop")
    public String stop(){
        return "STOP";
    }

    @RequestMapping("/addEvent")
    public void addEvent(){
        System.out.println("Got an Event and will be added to events");
        sampleService.addEvent();
    }

    @RequestMapping("/getEvent/{eventId}")
    public EventDetails getEvent(@PathVariable("eventId") String eventId) throws MalformedURLException {
        return sampleService.getEventFromService(eventId);
    }

    @RequestMapping("/getEventDetails")
    public List<EventDetails> getEventDetails(){
        return sampleService.getEventDetails();
    }
}
