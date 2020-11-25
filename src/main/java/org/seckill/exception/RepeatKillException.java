package org.seckill.exception;

/**
 * @author : Knight
 * @program : seckill
 * @description : 重复秒杀异常（运行时异常）
 * @date : 2020-11-22 14:54
 **/
public class RepeatKillException extends SeckillException{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
