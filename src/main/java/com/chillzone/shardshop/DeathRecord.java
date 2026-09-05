package com.chillzone.shardshop;

public record DeathRecord(
    String dimension,
    double x,
    double y,
    double z,
    float yaw,
    float pitch,
    long timestamp,
    String cause
) {}
