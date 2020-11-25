package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * @author : Knight
 * @program : seckill
 * @description : 秒杀业务接口 ： 在 “使用者” 角度设计接口：
 *                三个方面： 方法定义粒度 | 参数简练直接 | 返回类型：（return 类型 或 异常）L
 * @date : 2020-11-22 13:50
 **/
public interface SeckillService {

    /**
    * @Description:  查询所有秒杀记录
    * @Param: []
    * @return: java.util.List<org.seckill.entity.Seckill>
    */
    List<Seckill> getSeckillList() ;

    /**
    * @Description:  查询单个秒杀记录
    * @Param: [seckillId]
    * @return: org.seckill.entity.Seckill
    */
    Seckill getById(long seckillId) ;

    /**
    * @Description:  暴露秒杀接口：
     *               秒杀开启时，输出秒杀接口的地址，
                     否则输出系统时间和秒杀时间
    * @Param: [seckillId]
    * @return: org.seckill.dto.Exposer
    */
    Exposer exportSeckillUrl(long seckillId) ;

    /**
    * @Description: 执行秒杀操作
    * @Param: [seckillId, userPhone, md5]
    * @return: org.seckill.dto.SeckillExecution
    */
    SeckillExecution executeSeckill(long seckillId , long userPhone , String md5)
            throws SeckillException , RepeatKillException , SeckillCloseException ;

    /**
    * @Description:
    * @Param: [seckillId, userPhone, md5]
    * @return: org.seckill.dto.SeckillExecution
    */
    SeckillExecution executeSeckillProcedure(long seckillId , long userPhone , String md5)
            throws SeckillException , RepeatKillException , SeckillCloseException ;
}
