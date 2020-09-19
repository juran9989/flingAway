package com.fling.service;

import com.fling.controller.FlingController;
import com.fling.dto.*;
import com.fling.repo.FlingMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class FlingService {

    private final FlingMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(FlingController.class);

    @Autowired
    public FlingService(FlingMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 뿌리기
     * @param userId 사용자 ID
     * @param roomId 대화방 ID
     * @param amt 금액
     * @param cnt 뿌릴 인원수
     * @return 뿌리기로 채번된 토큰
     */
    public String flingAway(int userId, String roomId, long amt, int cnt) {

        log.debug("***** FlingService flingAway start *****");

        String token = null;
        String temp;
        String flingId;
        String pgName = "FlingService.flingAway";
        long balance = amt; //분배 후 잔액

        /*---------- 1.뿌리기 마스터 등록 ----------*/
        mapper.lockSelectToken(roomId); //뿌리기 작업 진행중 해당 룸 lock

        /* 1.1 토큰 생성 */
        while (token == null) {

            temp = RandomStringUtils.randomAlphanumeric(3);

            if (!temp.equals(mapper.selectToken(roomId, temp))) {
                token = temp;
            }
        }

        /* 1.2 테이블 등록 */
        flingId = mapper.insertFlingAwayMst(userId, roomId, amt, cnt, token, pgName);


        /*---------- 2.뿌리기 금액 마스터 등록 ----------*/
        for (int i = 1; i <= cnt; i++) {

            if (i == cnt) { // IF 마지막 루프라면
                mapper.insertFlingAwayAmtMst(userId, flingId, i, balance, pgName); //잔여금액을 전부 셋팅
                log.trace("insert amt = " + balance);
                break;
            }

            long divAmt = RandomUtils.nextLong(1, balance - (cnt - i) + 1);  // 1원 ~ (잔여금 - 잔여인원수) 미만이라서 1더함
            mapper.insertFlingAwayAmtMst(userId, flingId, i, divAmt, pgName);
            balance -= divAmt;
            log.trace("insert amt = " + divAmt);
        }

        /*---------- 3.뿌리기 멤버 마스터 등록 ----------*/
        mapper.insertFlingAwayMbrMst(userId, flingId, roomId, pgName);

        /*---------- 4.뿌리기 금액 출금 ----------*/
        log.info("사용자 출금 처리 API 호출 부분.");
        log.info("userId={}, amt={}", userId, amt);
        log.info("사용자 출금 처리 API 호출 부분.");


        log.debug("***** FlingService flingAway end *****");

        return token;

    }

    /**
     * 받기
     * @param userId 사용자 ID
     * @param flingDto 뿌리기마스터테이블
     * @return 사용자에게 주어질 금액
     */
    public long pickUp(int userId, FlingAwayMst flingDto) {

        log.debug("***** FlingService pickup start *****");

        LockSelectAmtMst lockDto;
        String pgName = "FlingService.pickUp";

        /* 1.뿌리기 금액 마스터 수정 */
        lockDto = mapper.lockselectAmtMst(flingDto.getFlingId());
        mapper.updateFlingAwayAmtMst(userId, flingDto.getFlingId(), lockDto.getSeq(), pgName);

        /* 2.뿌리기 멤버 마스터 수정 */
        mapper.updateFlingAwayMbrMst(userId, flingDto.getFlingId(), lockDto.getSeq(), pgName);

        /* 3.뿌리기 마스터 수정 */
        if (flingDto.getPeopleCnt() == lockDto.getSeq()) {
            String statCd = "2"; //1:진행중 2:받기완료 3:타임아웃종료
            mapper.updateFlingAwayMst(userId, flingDto.getFlingId(), pgName, statCd);
        }

        /* 4.받기 완료 후 송금 */
        log.info("사용자 송금 처리 API 호출 부분.");
        log.info("userId={}, amt={}", userId, lockDto.getAmt());
        log.info("사용자 송금 처리 API 호출 부분.");

        log.debug("***** FlingService pickup end *****");

        return lockDto.getAmt();

    }


    /**
     * 조회
     * @param userId 사용자 ID
     * @param roomId 대화방 ID
     * @param token 뿌리기 토큰
     * @return 조회 결과
     */
    public OutGet getFling(int userId, String roomId, String token) {

        log.debug("***** FlingService get start *****");

        List<SubGet> subGetList;
        SelectGet selectget;
        OffsetDateTime sevenMinusDay = OffsetDateTime.now().minusDays(7);


        /* 1.조회1 - 뿌린시각, 뿌린금액, 받기완료된금액 */
        selectget = mapper.selectGet1(roomId, token);

        if (selectget == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "요청하신 Token 의 뿌리기가 존재하지 않습니다.");
        }

        if (userId != selectget.getFlingUserId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "뿌린 본인만 조회가 가능합니다.");
        }

        if (sevenMinusDay.compareTo(selectget.getFlingDt()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "7일이 지난 뿌리기는 조회가 불가능합니다.");
        }

        /* 2.조회2 - 받은금액, 받은사용자아이디 */
        subGetList = mapper.selectGet2(roomId, token);


        OutGet result = OutGet.builder()
                .flingDt(selectget.getFlingDt())
                .flingAmt(selectget.getFlingAmt())
                .pickupAmt(selectget.getPickupAmt())
                .subGetList(subGetList)
                .build();

        log.debug("***** FlingService get end *****");

        return result;

    }

}
