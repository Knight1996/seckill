package org.seckill.exception;

/**
 * @author : Knight
 * @program : seckill
 * @description : 秒杀关闭异常
 * @date : 2020-11-22 14:57
 **/
public class SeckillCloseException extends SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
