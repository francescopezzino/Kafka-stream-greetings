package com.kafkastream.demo.serdes;

import com.kafkastream.demo.domain.Greeting;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class SerdesFactory  {

    public static Serde<Greeting> greetingSerdes() {
        return new GreetingSerdes();
    }
}
