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

#### Consume Messages

- This  cmd  take care of logging in to the Kafka container.

```
docker exec -it broker bash
```
- Command to consume messages from the Kafka topic.

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings_uppercase
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
