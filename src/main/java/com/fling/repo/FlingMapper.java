package com.fling.repo;

import com.fling.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlingMapper {

    FlingAwayMst selectOneFlingAwayMst(
            @Param("roomId") String roomId,
            @Param("token") String token);

    FlingAwayMbrMst selectOneFlingAwayMbrMst(
            @Param("flingId") String flingId,
            @Param("userId") int userId);

    String selectToken(
            @Param("roomId") String roomId,
            @Param("token") String token);

    String insertFlingAwayMst(
            @Param("userId") int userId,
            @Param("roomId") String roomId,
            @Param("amt") long amt,
            @Param("cnt") int cnt,
            @Param("token") String token,
            @Param("pgName") String pgName);

    void insertFlingAwayAmtMst(
            @Param("userId") int userId,
            @Param("flingId") String flingId,
            @Param("seq") int seq,
            @Param("amt") long amt,
            @Param("pgName") String pgName);

    void insertFlingAwayMbrMst(
            @Param("userId") int userId,
            @Param("flingId") String flingId,
            @Param("roomId") String roomId,
            @Param("pgName") String pgName);

    SelectGet selectGet1(
            @Param("roomId") String roomId,
            @Param("token") String token);

    List<SubGet> selectGet2(
            @Param("roomId") String roomId,
            @Param("token") String token);

    void updateFlingAwayAmtMst(
            @Param("userId") int userId,
            @Param("flingId") String flingId,
            @Param("seq") int seq,
            @Param("pgName") String pgName);

    void updateFlingAwayMbrMst(
            @Param("userId") int userId,
            @Param("flingId") String flingId,
            @Param("seq") int seq,
            @Param("pgName") String pgName);

    void updateFlingAwayMst(
            @Param("userId") int userId,
            @Param("flingId") String flingId,
            @Param("pgName") String pgName,
            @Param("statCd") String statCd);

    void lockSelectToken(
            @Param("roomId") String roomId);

    LockSelectAmtMst lockselectAmtMst(
            @Param("flingId") String flingId);

    List<SelectExpireTargetOut> selectExpireTarget();

    int selectCntFlingAwayMst();

    long selectTotAmtFlingAwayMstByToken(
            @Param("token") String token);

    int selectCntFlingAwayAmtMstByToken(
            @Param("token") String token);

    int selectCntPickedUpFlingAwayAmtMstByToken(
            @Param("token") String token);


}
