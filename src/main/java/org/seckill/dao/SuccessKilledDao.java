package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * @author : Knight
 * @program : seckill
 * @description : 秒杀成功明细DAO接口
 * @date : 2020-11-20 16:12
 **/
public interface SuccessKilledDao {

    /**
    * @Description:  插入秒杀购买明细，可过滤重复：
    * @Param: [seckillId, userPhone]
    * @return: int
     * 返回结果表示插入的行数
    */
    int insertSuccessKilled(@Param("seckillId") long seckillId , @Param("userPhone") long userPhone) ;

    /**
    * @Description:  根据 id 查询 SuccessKilled 并携带秒杀产品对象实体：
    * @Param: [seckillId]
    * @return: org.seckill.entity.SuccessKilled
    */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId , @Param("userPhone")long userPhone) ;
}
