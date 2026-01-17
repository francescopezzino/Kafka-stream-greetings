package com.kafkastream.demo.serdes;

import com.kafkastream.demo.domain.Greeting;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class SerdesFactory  {

    public static Serde<Greeting> greetingSerdes() {
        return new GreetingSerdes();
    }

    /**
     * For any other new type of record (representing a Json) just create another factory method with the new record type :
     * public static Serde<NewType> newTypeSerdesUsingGenerics() { }
     */
    public static Serde<Greeting> greetingSerdesUsingGenerics() {
        JsonSerializer<Greeting> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Greeting> jsonDeserializer = new JsonDeserializer<>(Greeting.class);
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }
}
