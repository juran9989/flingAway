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
public class FlingAwayMst {
    String flingId;
    String roomId;
    String token;
    int flingUserId;
    OffsetDateTime flingDt;
    OffsetDateTime endDt;
    long flingAmt;
    int peopleCnt;
    String flingStatCd;
}

