package com.xinghe.seckill.service;

import com.xinghe.seckill.dto.Exposer;
import com.xinghe.seckill.dto.SeckillExecution;
import com.xinghe.seckill.entity.Seckill;
import com.xinghe.seckill.exception.RepeatKillException;
import com.xinghe.seckill.exception.SeckillCloseException;
import com.xinghe.seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {
    /**
     * 查询所有秒杀记录
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启是输出秒杀借口地址，否则输出系统时间和秒杀时间
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws RepeatKillException, SeckillCloseException, SeckillException;
}
