package org.seckill.exception;

/**
 * @author : Knight
 * @program : seckill
 * @description : 秒杀相关业务异常
 * @date : 2020-11-22 14:59
 **/
public class SeckillException extends Exception{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
