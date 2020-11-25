package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Knight
 * @program : seckill
 * @description : SeckillService接口的实现类
 * @date : 2020-11-22 16:30
 **/

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass()) ;

    /**注入Service依赖
     */
    @Autowired
    private SeckillDao seckillDao ;

    @Autowired
    private SuccessKilledDao successKilledDao ;

    @Autowired
    private RedisDao redisDao ;

    /** md5 盐值字符串，用于混淆MD5：
     */
    private final String salt = "&7#as2N$%($312Ezk" ;

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes()) ;
        return md5 ;
    }

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0 , 4) ;
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId) ;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {

        // 优化点：缓存优化：超时基础上维护一致性：
        // 1、访问Redis：
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            // 2、访问数据库：
            seckill = seckillDao.queryById(seckillId) ;
            if(seckill == null){
                return new Exposer(false , seckillId) ;
            }
            else{
                // 3、放入redis:
                redisDao.putSeckill(seckill) ;
            }
        }

        // 秒杀开始时间
        Date startTime = seckill.getStartTime() ;
        // 秒杀结束时间
        Date endTime = seckill.getEndTime() ;
        // 当前时间
        Date nowTime = new Date() ;

        if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false , seckillId , nowTime.getTime() , startTime.getTime() , endTime.getTime()) ;
        }

        //加密算法：转化特定字符串的过程，不可逆：
        String md5 = getMD5(seckillId);
        return new Exposer(true , md5 ,seckillId);
    }

    @Override
    @Transactional
    /**
    * 使用注解控制事务方法的优点：
    *  1：开发团队达成一致约定，明确标定事务方法的编程风格。
     * 2：保证事务方法的执行时间尽可能短，不要穿插其余网络操作（RPC/HTTP请求）。
     * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务操作。
    */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("Seckill Data Rewrite") ;
        }

        // 执行秒杀逻辑：减库存 + 记录购买行为
        Date nowTime = new Date() ;

        try {
            //记录购买行为：
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //唯一的验证：seckillId, userPhone
            if(insertCount <= 0){
                // 重复秒杀：
                throw new RepeatKillException("Seckill Repeated") ;
            }
            else{
                //减库存，热点商品竞争：
                int updataCount = seckillDao.reduceNumber(seckillId , nowTime) ;

                if(updataCount <= 0){
                    // 没有更新到记录：秒杀结束 rollback:
                    throw new SeckillCloseException("Seckill Closed") ;
                }
                else{
                    // 秒杀成功：commit:
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId , SeckillStateEnum.SUCCESS , successKilled) ;
                }
            }
        } catch (RepeatKillException e1) {
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage() , e);
            //所有编译期异常 转化为 运行期异常：
            throw new SeckillException("Seckill Inner Error:" + e.getMessage()) ;
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId , long userPhone , String md5)
            throws SeckillException , RepeatKillException , SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        // 执行存储过程，result被赋值
        try {
            seckillDao.killByProceduce(map) ;
            // 获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
