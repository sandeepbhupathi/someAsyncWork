package com.example.demonew1.service;

import com.example.demonew1.domain.EventDetails;
import com.example.demonew1.domain.SampleThinMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class SampleService {

    private ConcurrentHashMap<SampleThinMessage,String> eventMap
            = new ConcurrentHashMap<SampleThinMessage,String>();
    @Autowired
    private RestTemplate restTemplate;
    private List<EventDetails> eventDetails = new ArrayList<>();

    @Scheduled(fixedDelay = 10000)
    public void generateEvents(){
        System.out.println("Adding an Event");
        addEvent();
    }

    //@Scheduled(fixedDelay = 11000)
    public void checkEvents(){
        System.out.println("Checking Events");
        System.out.println(eventMap);
    }

    public void addEvent(){
        String eventId = UUID.randomUUID().toString();
        String resource = "http://localhost:9090/getEvent/"+eventId;
        eventMap.put(new SampleThinMessage(eventId,resource),"NO ERROR");
    }

    @Scheduled(fixedDelay = 100000)
    public void processEvents(){
        System.out.println("Start of invoke Event");
        System.out.println("Before Invoke "+eventMap.size());
        ConcurrentHashMap<SampleThinMessage,String> processingEvents = new ConcurrentHashMap<>(eventMap);
        eventMap.clear();
        System.out.println(String.format("Working on clone clone size: %d, Actual size: %d",processingEvents.size(),eventMap.size()));
        System.out.println("Invoke for Events"+processingEvents);
        CompletableFuture.supplyAsync(invokeApiAsBatch(processingEvents))
                         .exceptionally(handleException())
                         .thenApply(saveEventResponseToDB())
                         .thenApply(handleFailedEvents(processingEvents));
        System.out.println("Sent as Background");
    }

    private Function<Boolean, Boolean> handleFailedEvents(ConcurrentHashMap<SampleThinMessage, String> processingEvents) {
        return (completeList)->{
            List<SampleThinMessage> sampleThinMessageList = processingEvents.entrySet().stream().filter((eachEvent)->{
                return !eachEvent.getValue().equalsIgnoreCase("NO ERROR");
            }).map((eachEvent)-> {return eachEvent.getKey();}).collect(Collectors.toList());
            if(sampleThinMessageList.isEmpty()){
                System.out.println("Event Failed:"+sampleThinMessageList);
            }
            if(sampleThinMessageList.size()>2){
                System.out.println("More Failures going to exception");
            }
            return completeList;
        };
    }

    private Function<Throwable, List<EventDetails>> handleException() {
        return (ex)->{
            System.out.println("Something went wrong will retry");
            return new ArrayList<EventDetails>();
        };
    }

    private Function<List<EventDetails>, Boolean> saveEventResponseToDB() {
        return (saveEventResponseList)->{
            System.out.println("Saving into DB");
           return eventDetails.addAll(saveEventResponseList);
        };
    }

    private Supplier<List<EventDetails>> invokeApiAsBatch(ConcurrentHashMap<SampleThinMessage, String> processingEvents) {
        return ()->{
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            List<Callable<EventDetails>> eventDetailsList = new ArrayList<>();
            processingEvents.entrySet().stream().forEach((eachEvent)->{
                eventDetailsList.add(()->{
                    return getEventFromUrl(eachEvent.getKey().getResource());
                });
            });
            try {
                List<Future<EventDetails>> futures = executorService.invokeAll(eventDetailsList);
                return futures.stream().map((eachF)->{
                    try {
                        EventDetails eventDetails = eachF.get();
                        if(eventDetails.getEventMaster().getUserId()%30==0){
                            throw new RuntimeException("Entire Batch will be failed");
                        }
                        return eventDetails;
                    } catch (InterruptedException | ExecutionException ex) {
                       throw new RuntimeException(ex);
                    }
                }).collect(Collectors.toList());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    public List<EventDetails> getEventDetails(){
        return eventDetails;
    }

    public EventDetails getEventFromService(String eventId) throws MalformedURLException {
        System.out.println("Invoking "+String.format("http://localhost:9090/getEvent/%s",eventId));
        return restTemplate.getForObject(
                String.format("http://localhost:9090/getEvent/%s",eventId), EventDetails.class);
    }

    public EventDetails getEventFromUrl(String url) throws MalformedURLException {
        System.out.println("Invoking "+url);
        return restTemplate.getForObject(url, EventDetails.class);
    }
}
