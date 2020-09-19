drop table if exists room_mbr_mst;
create table room_mbr_mst
(
    room_id        varchar(12),
    user_id        integer,
    constraint room_mbr_mst_pk primary key (room_id, user_id)
);

comment on table room_mbr_mst is '대화방 참여자 마스터(가정)';
comment on column room_mbr_mst.room_id        is '대화방 ID';
comment on column room_mbr_mst.user_id        is '사용자 ID';

insert into room_mbr_mst(room_id, user_id)
select 'R' || lpad((t/10)::text, 11, '0') as room, t from generate_series(10, 199) as t
