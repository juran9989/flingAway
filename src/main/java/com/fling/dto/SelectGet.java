package com.fling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectGet {
    String flingId;
    OffsetDateTime flingDt;
    long flingAmt;
    long pickupAmt;
    int flingUserId;
}