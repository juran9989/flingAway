package com.fling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlingAwayMbrMst {
    String flingId;
    int flingUserId;
    String pickupYn;
    int seq;
}