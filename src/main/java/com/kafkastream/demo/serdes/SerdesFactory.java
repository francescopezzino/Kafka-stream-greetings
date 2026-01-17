package com.kafkastream.demo.serdes;

import com.kafkastream.demo.domain.Greeting;
import org.apache.kafka.common.serialization.Serde;

public class SerdesFactory  {

    public static Serde<Greeting> greetingSerdes() {
        return new GreetingSerdes();
    }
}
