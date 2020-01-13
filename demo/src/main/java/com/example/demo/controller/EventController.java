package com.example.demo.controller;

import com.example.demo.domain.EventDetails;
import com.example.demo.service.EventDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {
    @Autowired
    private EventDetailService eventDetailService;

    @RequestMapping("/getEvent/{eventId}")
    public EventDetails getEventDetails(@PathVariable("eventId") String eventId) throws InterruptedException {
        return eventDetailService.getUserEventDetails(eventId);
    }
}
