package com.example.demo.service;

import com.example.demo.domain.EventDetails;
import com.example.demo.domain.EventMaster;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EventDetailService {

    public EventDetails getUserEventDetails(String eventId) throws InterruptedException {
        Random random = new Random();
        int userId = random.nextInt(1000);
        String userAction = "User "+userId+" has performed some random action";
        Thread.sleep(1000);
        return new EventDetails(eventId,new EventMaster(userId,userAction));
    }
}
