# Kafka Streams API for Developers


## Set up Kafka Environment using Docker (using the docker-compose.yml)

- This cmd set up the Zookeeper and Kafka Broker in your local environment

```
docker-compose up
```

### Verify the Local Kafka Environment

- Run this command

```
docker ps
```

- Containers List from Portainer

```
NAME        IMAGE                                   COMMAND                  CREATED               IP ADDRESS   PUBLISHED PORTS        
broker      confluentinc/cp-server:7.1.0            "/etc/confluent/dock…"   2026-01-15 15:46:20   172.30.0.3   9092:9092 9101:9101    
zookeeper   confluentinc/cp-zookeeper:7.1.0         "/etc/confluent/dock…"   2026-01-15 15:45:27   172.30.0.2   2181:2181              
```

### Interacting with Kafka

#### Produce Messages

- This  command should take care of logging in to the Kafka container.

```
docker exec -it broker bash
```

- Command to produce messages in to the Kafka topic.

```
kafka-console-producer --broker-list localhost:9092 --topic greetings
```

#### Publish to **greetings** topic with key and value

```
kafka-console-producer --broker-list localhost:9092 --topic greetings --property "key.separator=-" --property "parse.key=true"

```

- instantiate the producer with the following, and pass a key-value:
```
[appuser@broker ~]$ kafka-console-producer --broker-list localhost:9092 --topic greetings --property "key.separator=-" --property "parse.key=true"
>thisisthekey-this is the value

```
- Console output
```
[GreetingsStream]: thisisthekey, this is the value
[modifiedStream]: THISISTHEKEY, THIS IS THE VALUE

```

- Publish to **greetings-italian** topic with key and value

```
 kafka-console-producer --broker-list localhost:9092 --topic greetings_italian --property "key.separator=-" --property "parse.key=true"
```

- Consumer output
```
[appuser@broker ~]$ kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings_uppercase
THIS IS THE VALUE
```

#### Consume Messages

- This  cmd  take care of logging in to the Kafka container.

```
docker exec -it broker bash
```
- Command to consume messages from the Kafka topic.

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings_uppercase
```

- Command to consume with Key

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings_uppercase --from-beginning -property "key.separator= - " --property "print.key=true"
```
### List Topics

- This  cmd  take care of logging in to the Kafka container.

```
docker exec -it broker bash
```

- Command to list the topics.

```
kafka-topics --bootstrap-server localhost:9092 --list
```



### Kafka Stream Operators: org.apache.kafka.streams.kstream.KStream
#### filter and filterNot
```
filter(org.apache.kafka.streams.kstream.Predicate<? super K,? super V>)

This operator is used to drop elements in the kafka stream that does not meet a certain criteria
This opearator takes in a Predicate Functional interface as an input and apply the predicate against the input
    
    public KStream<K, V> filter(Predicate<? super K, ? super V> predicate) {
        return this.filter(predicate, NamedInternal.empty());
    }

    // Perform the enrichment (to uppercase)
    var modifiedStream = greetingsStream
            // any value with lenght > 5 will ignored and not sent to kafka topic GREETINGS_UPPERCASE
            .filter((key, value) -> value.length() > 5) 
            .mapValues(value -> value.toUpperCase());    //.mapValues((readOnlyKey, value) -> value.toUpperCase())
    modifiedStream.print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));
```


#### map
```
This operator is used when we have to transform the key and value to another form

    var modifiedStream = greetingsStream
            .map((key, value) -> KeyValue.pair(key.toUpperCase(), value.toUpperCase()));
    modifiedStream.print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));

```

#### mapValues
```
This is used when we have a usecae to transform just the values in the Kafka Stream

    var modifiedStream = greetingsStream
            .mapValues((readOnlyKey, value) -> value.toUpperCase())
    modifiedStream.print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));
```

#### flatMap
```
This operator can be used when a single event is going to create multiple possible events downstream
The flat map, "flatten" the collection, sending each as individual streamed event

    var uppercaseStream = greetingsStream.filter((key, value) -> value.length() > 5)
            .flatMap((key, value) -> {
                var newValue = Arrays.asList(value.split(""));  // holds the list of individual characters (e.g. for a String)
                var keyValueList = newValue.stream()
                        .map(t -> KeyValue.pair(key.toUpperCase(), t)).collect(Collectors.toList());
                return keyValueList;
            });
```

#### flatMapValues
```
Very similar to flatMap but we just access and change the values

    var uppercaseStream = greetingsStream.filter((key, value) -> value.length() > 5)
            .flatMapValues((keyOnlyKey, value) -> {
                var newValue = Arrays.asList(value.split(""));  // holds the list of individual characters (e.g. for a String)
                return newValue;
            });
```

#### merge
```
This operator is used to combine two independent Kafka Streams read from two different Topics into a single Kafka Stream.
We want to channel this data from two different topics into a single topic

    var uppercaseStream = greetingsStream.filter((key, value) -> value.length() > 5)
            .flatMapValues((keyOnlyKey, value) -> {
                var newValue = Arrays.asList(value.split(""));  // holds the list of individual characters (e.g. for a String)
                return newValue;
            });
```