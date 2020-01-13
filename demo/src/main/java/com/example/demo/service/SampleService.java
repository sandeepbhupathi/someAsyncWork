package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
public class SampleService {
    public static String BATCH_STAT = "NOTRUNNING";
    public String getBatchStat(){
        return BATCH_STAT;
    }

    public void invokeAsync(){
        if(!BATCH_STAT.equalsIgnoreCase("RUNNING")){
            BATCH_STAT="RUNNING";
            CompletableFuture completableFuture = CompletableFuture.supplyAsync(()->{
                try {
                    Thread.sleep(10000);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return Arrays.asList(1,2,3,4,5);
            }).thenApply((stokList)->{
                try {
                    Thread.sleep(10000);
                    return Arrays.asList(6,7,8,9,10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }).thenAccept((stockPriceList)->{
                if(stockPriceList!=null){
                    System.out.println(stockPriceList.stream().map((each)->{
                        return each*2;
                    }).distinct());
                }
                System.out.println(":::::::::::::::::::::::::");
            }).thenRun(()->BATCH_STAT="NOTRUNNING");

            System.out.println(completableFuture.isDone());
        }else {
            System.out.println("Will not invoke as an already running batch");
        }
    }

    public void invokeAsync2(){
        CompletableFuture completableFuture = CompletableFuture.supplyAsync(()->{
            System.out.println("First");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.asList(1,2,3,4,5);
        }) .thenApply((i)->{
            System.out.println("Sec");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Three"+i);
            return Arrays.asList(6,7,8,9,10);
        }).thenAccept((stockPriceList)->{
            if(stockPriceList!=null){
                System.out.println(stockPriceList.stream().map((each)->{
                    return each*2;
                }).count());
            }
            System.out.println(":::::::::::::::::::::::::");
        });

        System.out.println("Done First");
    }
}
