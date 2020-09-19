package com.fling.task;

import com.fling.dto.SelectExpireTargetOut;
import com.fling.repo.FlingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 배치 작업 위한 태스크 서비스
 */
@Service
public class FlingTaskService {

    private static final Logger log = LoggerFactory.getLogger(FlingTaskService.class);
    private final FlingMapper mapper;

    @Autowired
    public FlingTaskService(FlingMapper mapper) {
        this.mapper = mapper;
    }

    @Scheduled(fixedRate = 60000)
    public void expireToken() {
        log.info("토큰 만료 배치] 시작");

        List<SelectExpireTargetOut> targets = mapper.selectExpireTarget(); // 만료 대상 조회


        for (SelectExpireTargetOut t : targets) {
            String statCd = "3"; //1:진행중 2:받기완료 3:타임아웃종료
            mapper.updateFlingAwayMst(99999999, t.getFlingId(), "FlingTaskService.expireToken", statCd);

            if (t.getBalance() > 0) {
                log.info("토큰 만료 배치] 사용자 송금 처리 API 호출 부분.");
                log.info("토큰 만료 배치] userId={}, amt={}", t.getFlingUserId(), t.getBalance());
                log.info("토큰 만료 배치] 사용자 송금 처리 API 호출 부분.");
            }
        }

        log.info("토큰 만료 배치] 종료");
    }

}
