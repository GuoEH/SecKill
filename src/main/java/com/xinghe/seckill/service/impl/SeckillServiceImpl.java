package com.xinghe.seckill.service.impl;

import com.xinghe.seckill.dao.SeckillDao;
import com.xinghe.seckill.dao.SuccessKilledDao;
import com.xinghe.seckill.dao.cache.RedisDao;
import com.xinghe.seckill.dto.Exposer;
import com.xinghe.seckill.dto.SeckillExecution;
import com.xinghe.seckill.entity.Seckill;
import com.xinghe.seckill.entity.SuccessKilled;
import com.xinghe.seckill.enums.SeckillStatEnum;
import com.xinghe.seckill.exception.RepeatKillException;
import com.xinghe.seckill.exception.SeckillCloseException;
import com.xinghe.seckill.exception.SeckillException;
import com.xinghe.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    private String salt = "asrq2rqtgq4wt6q3a$#25rag";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                String result = redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        logger.info("startTime={}", startTime.toString());
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException {
        if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        try {
            //记录购买行为,防止重复秒杀
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                throw new RepeatKillException("seckill repeated");
            } else {
                //减库存，热点商品竞争，减少行级锁的持有时间
                Date nowTime = new Date();
                int reduceNumber = seckillDao.reduceNumber(seckillId, nowTime);
                if (reduceNumber <= 0) {
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            e1.printStackTrace();
            throw e1;
        } catch (RepeatKillException e2) {
            e2.printStackTrace();
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
