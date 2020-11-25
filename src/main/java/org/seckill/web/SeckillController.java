package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author : Knight
 * @program : seckill
 * @description : MVC控制器
 * @date : 2020-11-23 10:08
 **/

@Controller //类似于 @Service   @Component 的注解 将当前的 Controller 放入 spring 容器中
@RequestMapping("/seckill")  //url:/模块/资源/{id}/细分   如 /seckill/list
public class SeckillController {

    private Logger logger = LoggerFactory.getLogger(this.getClass()) ;

    @Autowired
    private SeckillService seckillService ;

    /**
    * @Description:  获得秒杀的列表页
    * @Param: [model]
    * @return: java.lang.String
    */
    @RequestMapping(value = "/list" , method = RequestMethod.GET)
    public String list(Model model){

        // 获取列表页
        List<Seckill> list = seckillService.getSeckillList() ;
        model.addAttribute("list" , list) ;
        // list.jsp + model = ModelAndView
        return "list" ;
    }

    /**
    * @Description:  获得秒杀的详情页
    * @Param: [seckillId, model]
    * @return: java.lang.String
    */
    @RequestMapping(value = "/{seckillId}/detail" , method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId , Model model){
        if(seckillId == null){
            // 请求重定向 到 list 请求上
            return "redirect:/seckill/list" ;
        }
        Seckill seckill = seckillService.getById(seckillId) ;
        if(seckill == null){
            return "forward:/seckill/list" ;
        }
        model.addAttribute("seckill" , seckill) ;
        return "detail" ;
    }

    /**
    * @Description:  秒杀开始就输出秒杀接口地址
     *               否则输出系统时间和秒杀时间
    * @Param: [seckillId]
    * @return: org.seckill.dto.SeckillResult<org.seckill.dto.Exposer>
    */
    @RequestMapping(value = "/{seckillId}/exposer" ,
            method = RequestMethod.POST ,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){

        SeckillResult<Exposer> result ;

        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId) ;
            result = new SeckillResult<>(true , exposer) ;
        } catch (Exception e) {
            logger.error(e.getMessage() , e);
            result = new SeckillResult<>(false , e.getMessage()) ;
        }

        return result ;
    }

    /**
    * @Description:  执行秒杀操作
    * @Param: [seckillId, md5, phone]
    * @return: org.seckill.dto.SeckillResult<org.seckill.dto.SeckillExecution>
    */
    @RequestMapping(value = "/{seckillId}/{md5}/execution" ,
            method = RequestMethod.POST ,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId ,
                                                   @PathVariable("md5") String md5 ,
                                                   @CookieValue(value = "killPhone" , required = false) Long phone){

        // SpringMVC Valid:
        if(phone == null){
            return new SeckillResult<>(false , "未注册") ;
        }

        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId , phone , md5) ;
            return new SeckillResult<>(true , execution) ;
        } catch (SeckillCloseException e1){
            SeckillExecution execution = new SeckillExecution(seckillId , SeckillStateEnum.END);
            return new SeckillResult<>(true, execution);
        } catch (RepeatKillException e2){
            SeckillExecution execution = new SeckillExecution(seckillId , SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage() , e) ;
            SeckillExecution execution = new SeckillExecution(seckillId , SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<>(true, execution);
        }
    }

    /**
    * @Description:  获取秒杀系统时间
    * @Param: []
    * @return: org.seckill.dto.SeckillResult<java.lang.Long>
    */
    @RequestMapping(value = "/time/now" , method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult<>(true, now.getTime());
    }
}
