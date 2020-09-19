drop table if exists fling_away_mst;
create table fling_away_mst
(
    fling_id       varchar(12) constraint fling_away_mst_pk primary key,
    room_id        varchar(12),
    token          char(3),
    fling_user_id  integer,
    fling_dt       timestamp with time zone default CURRENT_TIMESTAMP not null,
    end_dt         timestamp with time zone,
    fling_amt      numeric(12),
    people_cnt     integer,
    fling_stat_cd  char(1) default '1',

    del_yn         varchar(1) default 'N',
    create_user_id integer,
    create_dt      timestamp with time zone default CURRENT_TIMESTAMP not null,
    create_pg_name varchar(100),
    update_user_id integer,
    update_dt      timestamp with time zone,
    update_pg_name varchar(100)
);

comment on table fling_away_mst is '뿌리기 마스터';
comment on column fling_away_mst.fling_id       is '뿌리기 ID';
comment on column fling_away_mst.room_id        is '대화방 ID';
comment on column fling_away_mst.token          is '토큰';
comment on column fling_away_mst.fling_user_id  is '뿌린사람';
comment on column fling_away_mst.fling_dt       is '뿌린일시';
comment on column fling_away_mst.end_dt         is '종료일시';
comment on column fling_away_mst.fling_amt      is '뿌린금액';
comment on column fling_away_mst.people_cnt     is '총받을사람수';
comment on column fling_away_mst.fling_stat_cd  is '뿌리기상태코드';

comment on column fling_away_mst.del_yn         is '삭제여부';
comment on column fling_away_mst.create_dt      is '생성일시';
comment on column fling_away_mst.create_user_id is '생성자 ID';
comment on column fling_away_mst.create_pg_name is '생성 프로그램 명';
comment on column fling_away_mst.update_dt      is '수정일시';
comment on column fling_away_mst.update_user_id is '수정자 ID';
comment on column fling_away_mst.update_pg_name is '수정 프로그램 명';

create sequence fling_id_seq
start 1
maxvalue 99999999999;
alter table fling_away_mst alter fling_id set default 'F' || lpad(nextval('fling_id_seq')::text, 11, '0');

