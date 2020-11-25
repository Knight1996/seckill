package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author : Knight
 * @program : seckill
 * @description : 秒杀库存Dao接口
 * @date : 2020-11-20 16:05
 **/
public interface SeckillDao {

    /**
    * @Description:  减库存
    * @Param: [seckillId, killTime]
    * @return: int
     * 返回1，表示更新的记录行数
    */
    int reduceNumber(@Param("seckillId") long seckillId , @Param("killTime") Date killTime );

    /**
    * @Description:  根据 id 查询 秒杀对象
    * @Param: [seckillId]
    * @return: org.seckill.entity.Seckill
    */
    Seckill queryById(long seckillId) ;

    /**
    * @Description:  根据偏移量 offset 来 查询秒杀商品列表
    * @Param: [offset, limit]
    * @return: java.util.List<org.seckill.entity.Seckill>
    */
    List<Seckill> queryAll(@Param("offset") int offset , @Param("limit") int limit) ;

    /**
    * @Description:  使用存储过程进行秒杀
    * @Param: [paramMap]
    * @return: void
    */
    void killByProceduce(Map<String,Object> paramMap);
}
