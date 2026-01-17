package com.kafkastream.demo.topology;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;


import java.util.Arrays;
import java.util.stream.Collectors;
@Slf4j
public class GreetingsTopology {

    public static String GREETINGS = "greetings";

    public static String GREETINGS_UPPERCASE = "greetings_uppercase";

    public static String GREETINGS_ITALIAN = "greetings_italian";

    public static Topology builTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Reading from the Kafka Topic - Uses Consumer API
//        var greetingsStream = builder.stream(GREETINGS,
//                Consumed.with(Serdes.String(), Serdes.String()));  // instructing the kafka stream to use a deSerializer

        // merge topics test
//        var greetingsItalianStream = builder.stream(GREETINGS_ITALIAN, Consumed.with(Serdes.String(), Serdes.String()));

        // Providing Default Serializer/Deserializer using Application Configuration
        KStream<String, String> greetingsStream = builder.stream(GREETINGS);
        KStream<String, String> greetingsItalianStream = builder.stream(GREETINGS_ITALIAN);

        var mergedStream = greetingsStream.merge(greetingsItalianStream);

        greetingsStream.print(Printed.<String, String>toSysOut().withLabel("GreetingsStream"));

/*      // merge topics test
        var modifiedStream = mergedStream.mapValues((readOnlyKey, value) -> value.toUpperCase());
        modifiedStream.print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));
*/

        // Perform the enrichment (to uppercase)
        var modifiedStream = mergedStream
                // .filterNot((key, value) -> value.length() > 5)
                .filter((key, value) -> value.length() > 5) // any value with lenght > 5 will ignored and not sent to kafka topic GREETINGS_UPPERCASE
                .peek((key, value) -> log.info("after filter : key  {}, value {}", key, value))
                .mapValues((readOnlyKey, value) -> value.toUpperCase())
                .peek((key, value) -> log.info("after filter : key  {}, value {}", key, value))    //.mapValues((readOnlyKey, value) -> value.toUpperCase())

                // using map
//                .map((key, value) -> KeyValue.pair(key.toUpperCase(), value.toUpperCase()));

                // using flatMap
                    .flatMap((key, value) -> {
                       var newValues = Arrays.asList(value.split(""));  // note we split on each character now
                       return newValues
                               .stream()
                               .map(val -> KeyValue.pair(key, val.toUpperCase()))
                               .toList();   // same as: .collect(Collectors.toList());
                    });

                // using flatMapValues
//                .flatMapValues((key, value) -> {
//                    var newValues = Arrays.asList(value.split(""));  // note we split on each character now
//                    return newValues
//                            .stream()
//                            .map(String::toUpperCase)
//                            .toList();   // same as: .collect(Collectors.toList());
//                });

        modifiedStream.print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));

/*        var uppercaseStream = greetingsStream.filter((key, value) -> value.length() > 5)
                .flatMap((key, value) -> {
                    var newValue = Arrays.asList(value.split(" "));
                    var keyValueList = newValue.stream()
                            .map(t -> KeyValue.pair(key.toUpperCase(), t)).collect(Collectors.toList());
                    return keyValueList;
                });

        var uppercaseStream = greetingsStream.filter((key, value) -> value.length() > 5)
                .flatMapValues((keyOnlyKey, value) -> {
                    var newValue = Arrays.asList(value.split(" "));  // holds the list of individual characters (e.g. for a String)
                    return newValue;
                });
*/

        // Writing back to a Kafka Topic - Uses Producer API
//        modifiedStream.to(GREETINGS_UPPERCASE,
//                Produced.with(Serdes.String(), Serdes.String()));  // instructing the kafka stream to use a serializer

        // Providing Default Serializer/Deserializer using Application Configuration
        modifiedStream.to(GREETINGS_UPPERCASE);

        return builder.build();
    }
}
