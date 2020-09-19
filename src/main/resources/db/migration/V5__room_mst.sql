drop table if exists room_mst;
create table room_mst
(
    room_id        varchar(12) constraint room_mst_pk primary key
);

comment on table room_mst is '대화방 마스터(가정)';
comment on column room_mst.room_id is '대화방 ID';

insert into room_mst(room_id)
select 'R' || lpad((t)::text, 11, '0') as room from generate_series(1, 19) as t

