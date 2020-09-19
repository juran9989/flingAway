drop table if exists fling_away_amt_mst;
create table fling_away_amt_mst
(
    fling_id       varchar(12),
    seq            integer,
    amt            numeric(12),
    pickup_yn      varchar(1) default 'N',
    pickup_user_id varchar(100),
    pickup_dt      timestamp with time zone,

    del_yn         varchar(1) default 'N',
    create_user_id integer,
    create_dt      timestamp with time zone default CURRENT_TIMESTAMP not null,
    create_pg_name varchar(100),
    update_user_id integer,
    update_dt      timestamp with time zone,
    update_pg_name varchar(100),

    constraint fling_away_amt_mst_pk primary key (fling_id, seq)
);

comment on table fling_away_amt_mst is '뿌리기 금액 마스터';
comment on column fling_away_amt_mst.fling_id       is '뿌리기 ID';
comment on column fling_away_amt_mst.seq            is '순번';
comment on column fling_away_amt_mst.amt            is '금액';
comment on column fling_away_amt_mst.pickup_yn      is '받기여부';
comment on column fling_away_amt_mst.pickup_user_id is '받은사람';
comment on column fling_away_amt_mst.pickup_dt      is '받은일시';

comment on column fling_away_amt_mst.del_yn         is '삭제여부';
comment on column fling_away_amt_mst.create_dt      is '생성일시';
comment on column fling_away_amt_mst.create_user_id is '생성자 ID';
comment on column fling_away_amt_mst.create_pg_name is '생성 프로그램 명';
comment on column fling_away_amt_mst.update_dt      is '수정일시';
comment on column fling_away_amt_mst.update_user_id is '수정자 ID';
comment on column fling_away_amt_mst.update_pg_name is '수정 프로그램 명';



