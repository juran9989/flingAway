package com.fling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutGet {
    OffsetDateTime flingDt;
    long flingAmt;
    long pickupAmt;
    List<SubGet> subGetList;
}
