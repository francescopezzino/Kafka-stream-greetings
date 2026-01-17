package com.kafkastream.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

record Greetings(String message, LocalDateTime timestamp) {}
