package com.kafkastream.demo.topology;

import com.kafkastream.demo.domain.Greeting;
import com.kafkastream.demo.serdes.SerdesFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

@Slf4j
public class GreetingsTopology {

    public static String GREETINGS = "greetings";

    public static String GREETINGS_UPPERCASE = "greetings_uppercase";

    public static String GREETINGS_ITALIAN = "greetings_italian";

    public static Topology builTopology() {
        StreamsBuilder streamBuilder = new StreamsBuilder();

        KStream<String, Greeting> mergedStream = getCustomGreetingUsingGenericsKStream(streamBuilder);
        mergedStream.print(Printed.<String, Greeting>toSysOut().withLabel("GreetingsStream"));

        var modifiedStream = mergedStream
                .mapValues((readOnlyKey, value) -> new Greeting(value.message().toUpperCase(), value.timestamp()));

        modifiedStream.print(Printed.<String, Greeting>toSysOut().withLabel("modifiedStream"));

        // Producer now uses Custom Serdes Serializer/Deserializer
        modifiedStream.to(GREETINGS_UPPERCASE, Produced.with(Serdes.String(), SerdesFactory.greetingSerdesUsingGenerics()));

        return streamBuilder.build();
    }

    private static KStream<String, String> getStringGreetingKStream (StreamsBuilder builder) {

        KStream<String, String> greetingsStream = builder.stream(GREETINGS);
        KStream<String, String> greetingsItalianStream = builder.stream(GREETINGS_ITALIAN);
        var mergedStream = greetingsStream.merge(greetingsItalianStream);
        return mergedStream;
    }

    /**
     * Custom Greeting stream, reads the value as JSON, and the JSON will be of type Greeting
     * @param streamBuilder
     * @return
     */
    private static KStream<String, Greeting> getCustomGreetingUsingGenericsKStream (StreamsBuilder streamBuilder) {

        var greetingsStream = streamBuilder.stream(GREETINGS, Consumed.with(Serdes.String(), SerdesFactory.greetingSerdesUsingGenerics()));
        var greetingsItalianStream = streamBuilder.stream(GREETINGS_ITALIAN, Consumed.with(Serdes.String(), SerdesFactory.greetingSerdesUsingGenerics()));
        var mergedStream = greetingsStream.merge(greetingsItalianStream);

        return mergedStream;
    }

}
