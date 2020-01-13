package com.example.demonew1.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class SampleThinMessage {

    private String eventId;
    private String resource;
}
