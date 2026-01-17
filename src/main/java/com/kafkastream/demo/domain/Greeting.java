package com.kafkastream.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * Enhanced Greeting Message, will be mapped with the Jackson ObjectMapper
 * @param message
 * @param timestamp
 *
 * JSON example:
 * {
 *    "message": "Good Morning 2026",
 *    "timeStamp": "2026-01-01T08:34:25.079385"
 * }
 *
 */
public record Greeting(String message, LocalDateTime timestamp) {}
