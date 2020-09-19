package com.fling.controller;

import com.fling.dto.*;
import com.fling.repo.FlingMapper;
import com.fling.service.FlingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/fling")
@Api(tags = "뿌리기 기능 콘트롤러")
public class FlingController {

    private final FlingService serviceFling;
    private final FlingMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(FlingController.class);

    @Autowired
    public FlingController(FlingService serviceFling, FlingMapper mapper) {
        this.serviceFling = serviceFling;
        this.mapper = mapper;
    }

    @PostMapping
    @ApiOperation("뿌리기")
    public OutFlingAway flingAway(@RequestHeader("X-USER-ID") int userId,
                                  @RequestHeader("X-ROOM-ID") String roomId,
                                  @RequestBody InFlingAway in
    ) {

        log.info("***** FlingController flingAway start *****");
        log.info("userId={}, roomId={}, amt={}, cnt={}", userId, roomId, in.getAmt(), in.getCnt());

        String token;

        /*---------- 1.입력값 체크 ----------*/
        //대화방 참여 인원 전체보다 뿌릴인원의 수가 큰 경우는 뿌리기가 불가능하도록 Front-end 에서 체크한다는 가정으로 체크조건 제외
        //대화방 참여 인원이 2명인 경우는 뿌리기가 불가능하도록 Front-end 에서 체크한다는 가정으로 체크조건 제외

        if (in.getAmt() < 1 || in.getAmt() > 999999999999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 뿌릴금액이 입력되었습니다.");
        }

        if (in.getCnt() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 뿌릴인원이 입력되었습니다.");
        }

        if (in.getAmt() < in.getCnt()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인원수 보다 적은 금액이 입력되었습니다.");
        }


        /*---------- 2.본 처리 ----------*/
        token = serviceFling.flingAway(userId, roomId, in.getAmt(), in.getCnt());
        log.info("token={}", token);

        /*---------- 3.출력값 셋팅 ----------*/
        OutFlingAway result = OutFlingAway.builder()
                .token(token)
                .build();

        log.info("***** FlingController flingAway end *****");
        log.info("result={}", result);

        return result;

    }

    @PutMapping
    @ApiOperation("받기")
    public OutPickUp pickUp(@RequestHeader("X-USER-ID") int userId,
                            @RequestHeader("X-ROOM-ID") String roomId,
                            @RequestBody InPickUp in) {

        log.info("***** FlingController pickUp start *****");
        log.info("userId={}, roomId={}, token={}", userId, roomId, in.getToken());

        FlingAwayMst flingDto;
        FlingAwayMbrMst flingMbrDto;
        long amt;

        /*---------- 1.입력값 체크 ----------*/
        flingDto = mapper.selectOneFlingAwayMst(roomId, in.getToken());

        if (flingDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "요청하신 뿌리기가 존재하지 않습니다.");
        }

        if (flingDto.getFlingUserId() == userId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "뿌린 사람은 받기가 불가능합니다.");
        }

        if (!flingDto.getFlingStatCd().equals("1")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "뿌리기 마감이 완료되었습니다.");
        }

        flingMbrDto = mapper.selectOneFlingAwayMbrMst(flingDto.getFlingId(), userId);

        if (flingMbrDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당뿌리기를 받을 수 있는 사용가 아닙니다.");
        }

        if (flingMbrDto.getPickupYn().equals("Y")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 받은 사용자입니다. 받기는 한번만 가능합니다.");
        }


        /*---------- 2.본 처리 ----------*/
        amt = serviceFling.pickUp(userId, flingDto);
        log.info("amt={}", amt);

        /*---------- 3.출력값 셋팅 ----------*/
        OutPickUp result = OutPickUp.builder()
                .amt(amt)
                .build();

        log.info("***** FlingController pickUp end *****");
        log.info("result={}", result);

        return result;

    }

    @GetMapping
    @ApiOperation("조회")
    public OutGet get(@RequestHeader("X-USER-ID") int userId,
                      @RequestHeader("X-ROOM-ID") String roomId,
                      InGet in) {

        log.info("***** FlingController get start *****");
        log.info("userId={}, roomId={}, token={}", userId, roomId, in.getToken());

        OutGet result;

        /*---------- 1.본 처리 ----------*/
        result = serviceFling.getFling(userId, roomId, in.getToken());

        log.info("***** FlingController get end *****");
        log.info("result={}", result);

        return result;

    }

}
