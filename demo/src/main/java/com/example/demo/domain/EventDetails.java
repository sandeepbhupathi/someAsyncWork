package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventDetails {
    private String eventId;
    private EventMaster eventMaster;
}
