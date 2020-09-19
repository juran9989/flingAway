drop table if exists fling_away_mbr_mst;
create table fling_away_mbr_mst
(
    fling_id       varchar(12),
    user_id        integer,
    pickup_yn      varchar(1) default 'N',
    seq            integer,

    del_yn         varchar(1) default 'N',
    create_user_id integer,
    create_dt      timestamp with time zone default CURRENT_TIMESTAMP not null,
    create_pg_name varchar(100),
    update_user_id integer,
    update_dt      timestamp with time zone,
    update_pg_name varchar(100),

    constraint fling_away_mbr_mst_pk primary key (fling_id, user_id)
);

comment on table fling_away_mbr_mst is '뿌리기 멤버 마스터';
comment on column fling_away_mbr_mst.fling_id       is '뿌리기 ID';
comment on column fling_away_mbr_mst.user_id        is '사용자 ID';
comment on column fling_away_mbr_mst.pickup_yn      is '받기여부';
comment on column fling_away_mbr_mst.seq            is '순번';

comment on column fling_away_mbr_mst.del_yn         is '삭제여부';
comment on column fling_away_mbr_mst.create_dt      is '생성일시';
comment on column fling_away_mbr_mst.create_user_id is '생성자 ID';
comment on column fling_away_mbr_mst.create_pg_name is '생성 프로그램 명';
comment on column fling_away_mbr_mst.update_dt      is '수정일시';
comment on column fling_away_mbr_mst.update_user_id is '수정자 ID';
comment on column fling_away_mbr_mst.update_pg_name is '수정 프로그램 명';



