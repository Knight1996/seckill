package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
   配置 spring 和 junit 整合 ，
   junit 启动时加载 springIOC 容器
   利用 spring-test , junit 两个依赖
*/
@RunWith(SpringJUnit4ClassRunner.class)
// 通过 注解 告诉junit  spring配置文件位置：
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入DAO依赖:
    @Resource
    private SeckillDao seckillDao ;

    @Test
    public void testReduceNumber() throws Exception {
        Date killTime = new Date() ;
        int updateCount = seckillDao.reduceNumber(1000L , killTime) ;
        System.out.println("updateCount = " + updateCount) ;
    }

    @Test
    public void testQueryById() throws Exception {
        long id = 1000L ;
        Seckill seckill = seckillDao.queryById(id) ;
        System.out.println(seckill.getName()) ;
        System.out.println(seckill);
    }

    @Test
    public void testQueryAll() throws Exception {
        List<Seckill> list = seckillDao.queryAll(0 , 100) ;
        for(Seckill seckill : list){
            System.out.println(seckill) ;
        }
    }
}