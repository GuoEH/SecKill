use seckill;

-- 秒杀库存表
create table seckill(
  `seckill_id` bigint not null auto_increment comment '商品库存ID',
  `name` varchar(120) not null comment '商品名称',
  `number` int not null comment '库存数量',
  `start_time` timestamp not null comment '秒杀开启时间',
  `end_time` timestamp not null comment '秒杀结束时间',
  `create_time` timestamp not null default current_timestamp comment '创建时间',
  primary key (seckill_id),
  key idx_start_time(start_time),
  key idx_end_time(end_time),
  key idx_create_time(create_time)
)engine=InnoDB auto_increment=1000 default charset=utf8 comment='秒杀库存表';

insert into seckill(name, number, start_time, end_time)
values
       ('1000元秒杀iPhone X',100,'2019-01-01 00:00:00','2019-01-05 23:59:59'),
       ('500元秒杀iPad Air',200,'2018-12-31 00:00:00','2019-01-04 23:59:59'),
       ('300元秒杀iPhone X',300,'2018-01-04 00:00:00','2019-01-10 23:59:59'),
       ('200元秒杀iPhone X',400,'2018-12-20 00:00:00','2018-12-31 23:59:59');

-- 秒杀成功明细表
create table success_killed(
  `seckill_id` bigint not null comment '秒杀商品ID',
  `user_phone` bigint not null comment '用户手机号',
  `state` tinyint not null default -1 comment '状态标识：-1无效；0成功；1已付款',
  `create_time` timestamp not null default current_timestamp comment '创建时间',
  primary key (seckill_id,user_phone),
  key idx_create_time(create_time)
)engine=InnoDB default charset=utf8 comment='秒杀成功明细表';