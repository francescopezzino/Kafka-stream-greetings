package com.kafkastream.demo.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

public class GreetingsTopology {

    public static String GREETINGS = "greetings";

    public static String GREETINGS_UPPERCASE = "greetings_uppercase";

    public static Topology builTopolog() {
        StreamsBuilder builder = new StreamsBuilder();
        // Reading from the Kafka Topic - Uses Consumer API
        var greetingsStream = builder.stream(GREETINGS, Consumed.with(Serdes.String(), Serdes.String()));
        greetingsStream.print(Printed.<String, String>toSysOut().withLabel("GreetingsStream"));

        // Perform the enrichment (to uppercase)
        var modifiedStream = greetingsStream.mapValues(value -> value.toUpperCase());    //.mapValues((readOnlyKey, value) -> value.toUpperCase())
        modifiedStream.print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));

        // Writing back to a Kafka Topic - Uses Producer API
        modifiedStream.to(GREETINGS_UPPERCASE, Produced.with(Serdes.String(), Serdes.String()));
        return builder.build();
    }
}
