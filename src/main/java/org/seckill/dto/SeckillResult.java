package org.seckill.dto;

/**
 * @author : Knight
 * @program : seckill
 * @description : 封装  json 结果
 * @date : 2020-11-23 10:30
 **/
public class SeckillResult<T> {

    /** 判断请求成功与否：
     */
    private boolean success ;

    /** 泛型类型数据
     */
    private T Date ;

    /** 错误信息
     */
    private String error ;

    public SeckillResult(boolean success, T date) {
        this.success = success;
        Date = date;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getDate() {
        return Date;
    }

    public void setDate(T date) {
        Date = date;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "SeckillResult{" +
                "success=" + success +
                ", Date=" + Date +
                ", error='" + error + '\'' +
                '}';
    }
}
