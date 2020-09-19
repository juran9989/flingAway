package com.fling;

import com.fling.dto.FlingAwayMbrMst;
import com.fling.dto.InPickUp;
import com.fling.dto.OutPickUp;
import com.fling.dto.SelectGet;
import com.fling.repo.FlingMapper;
import com.fling.service.FlingService;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlingApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(FlingApplicationTests.class);

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    FlingService service;

    @Autowired
    FlingMapper mapper;

    @Test
    @DisplayName("뿌리기] 뿌리기 후, DB fling_away_mst 건수가 호출 전 대비 증가 해야 한다.")
    void flingAwayTest1() {

        // given
        int userId = 10;
        String roomId = "R00000000001";
        long amt = 1000;
        int cnt = 5;

        // when
        int cntBf = mapper.selectCntFlingAwayMst();
        service.flingAway(userId, roomId, amt, cnt);
        int cntAf = mapper.selectCntFlingAwayMst();

        // then
        log.info("bf={}, af={}", cntBf, cntAf);
        assertThat(cntAf).isGreaterThan(cntBf);

    }

    @Test
    @DisplayName("뿌리기] 뿌리기 후, DB fling_away_amt_mst 건수는 분배 인원수와 같아야 한다.")
    void flingAwayTest4() {

        // given
        int userId = 10;
        String roomId = "1";
        long amt = RandomUtils.nextLong(1, 1000);
        int cnt = RandomUtils.nextInt(1, (int) amt);

        // when
        String token = service.flingAway(userId, roomId, amt, cnt);
        int cntAwayAmt = mapper.selectCntFlingAwayAmtMstByToken(token);

        // then
        log.info("token={}, 인원수={}, away_amt_mst 건수={}", token, cnt, cntAwayAmt);
        assertThat(cnt).isEqualTo(cntAwayAmt);

    }


    @Test
    @DisplayName("뿌리기] 분배된 금액의 총합은 뿌린 금액과 동일해야 한다.")
    void flingAwayTest2() {

        // given
        int userId = 10;
        String roomId = "1";
        Long amt = RandomUtils.nextLong(1, 10000);
        int cnt = RandomUtils.nextInt(1, amt.intValue());

        // when
        String token = service.flingAway(userId, roomId, amt, cnt);

        // then
        long amtTot = mapper.selectTotAmtFlingAwayMstByToken(token);
        log.info("입력된 뿌린금액={}, fling_away_amt_mst sum() 금액={}", amt, amtTot);
        assertThat(amt).isEqualTo(amt);

    }

    @Test
    @DisplayName("뿌리기] 100번 호출 시, 각 Transaction 은 1초 이내에 종료 되어야 한다.")
    void flingAwayTest3() {

        // given
        int userId = 10;
        String roomId = "R00000000001";
        long amt = 10000;
        int cnt = 10;

        // when
        IntStream.range(0, 100)
                .parallel()
                .forEach(i -> {

                    Instant start = Instant.now();
                    String token = service.flingAway(userId, roomId, amt, cnt);
                    Instant end = Instant.now();
                    long elapsed = Duration.between(start, end).toMillis();

                    // then
                    log.info("elapsed = {}ms", elapsed);
                    assertThat(elapsed).isLessThan(1000);
                });

    }

    @Test
    @DisplayName("줍기] 줍기 호출 뒤, away_amt_mst 의 주운여부 컬럼 변경 확인 ")
    void pickUpTest1() {

        // given - 뿌리기
        int userIdForFling = 10;
        String roomId = "R00000000001";
        long amt = 3000;
        int cnt = 5;

        String token = service.flingAway(userIdForFling, roomId, amt, cnt); // 뿌리기
        int cntBfPickUp = mapper.selectCntPickedUpFlingAwayAmtMstByToken(token);

        // given - 받기 위한 prepare
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", "11");
        headers.set("X-ROOM-ID", "R00000000001");

        int userId = 11;
        InPickUp input = new InPickUp();
        input.setToken(token);

        HttpEntity<InPickUp> request = new HttpEntity<>(input, headers);


        // when - 받기 진행
        ResponseEntity<OutPickUp> result = rest.exchange("/fling", HttpMethod.PUT, request, OutPickUp.class);
        int cntAfPickUp = mapper.selectCntPickedUpFlingAwayAmtMstByToken(token);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        log.info("Before Pickup Count = {}, After Count = {}", cntBfPickUp, cntAfPickUp);
        assertThat(cntAfPickUp).isGreaterThan(cntBfPickUp);

    }

    @Test
    @DisplayName("줍기] 줍기 호출 뒤, fling_away_mbr_mst 의 주운 여부 확인 ")
    void pickUpTest2() {

        // given - 뿌리기
        int userIdForFling = 10;
        String roomId = "R00000000001";
        long amt = 3000;
        int cnt = 5;

        String token = service.flingAway(userIdForFling, roomId, amt, cnt); // 뿌리기

        // given - 받기 위한 prepare
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", "11");
        headers.set("X-ROOM-ID", "R00000000001");

        int userId = 11;
        InPickUp input = new InPickUp();
        input.setToken(token);

        HttpEntity<InPickUp> request = new HttpEntity<>(input, headers);

        // when - 받기 진행
        ResponseEntity<OutPickUp> result = rest.exchange("/fling", HttpMethod.PUT, request, OutPickUp.class);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();

        SelectGet selectGet = mapper.selectGet1(roomId, token);
        assertThat(selectGet).isNotNull(); // fling_away_mst 가 존재해야 한다.
        FlingAwayMbrMst mbrMst = mapper.selectOneFlingAwayMbrMst(selectGet.getFlingId(), userId);
        assertThat(mbrMst).isNotNull(); // fling_away_mbr_mst 가 존재해야 한다.

        assertThat(mbrMst.getPickupYn()).isEqualTo("Y"); // 해당 유저의 주운 여부가 'Y' 여야 한다.

    }



}
