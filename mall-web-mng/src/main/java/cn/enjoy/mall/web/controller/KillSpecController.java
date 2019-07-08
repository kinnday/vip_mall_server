package cn.enjoy.mall.web.controller;

import cn.enjoy.core.utils.response.HttpResponseBody;
import cn.enjoy.mall.model.KillGoodsPrice;
import cn.enjoy.mall.service.manage.IKillSpecManageService;
import cn.enjoy.sys.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/killgoodsSpec")
public class KillSpecController extends BaseController {

    @Reference
    private IKillSpecManageService iKillSpecManageService;

    @GetMapping("/queryByPage")
    public HttpResponseBody queryByPage(String name){
        return HttpResponseBody.successResponse("ok", iKillSpecManageService.queryByPage(name,1,20));
    }

    @GetMapping("/detail")
    public HttpResponseBody detail(Integer id) {
        return HttpResponseBody.successResponse("ok", iKillSpecManageService.selectByPrimaryKey(id));
    }

    /**
     * 秒杀，就是新增一个订单；
     * 并发量很大时，会导致数据库崩盘，本质问题就是减轻数据库的压力
     * 1.减少查询数据库的频率，存入redis
     * 2.订单排队记录到rabbitMQ,依次处理
     * @param killGoodsPrice
     * @return
     */
    @PostMapping("/save")
    public HttpResponseBody save(KillGoodsPrice killGoodsPrice){
        if (killGoodsPrice.getId() == null || killGoodsPrice.getId() == 0){
            if (iKillSpecManageService.selectCountBySpecGoodId(killGoodsPrice.getSpecGoodsId()) > 0){
                return HttpResponseBody.failResponse("同一商品规格不能重复加入秒杀");
            }
            iKillSpecManageService.save(killGoodsPrice);
        } else {
            KillGoodsPrice killGoods = iKillSpecManageService.selectByPrimaryKey(killGoodsPrice.getId());
            if (killGoods.getStatus() ==1 && killGoods.getBegainTime().getTime() < System.currentTimeMillis()){
                iKillSpecManageService.flushCache(killGoods);
                return HttpResponseBody.failResponse("秒杀已运行，不支持修改");
            }

            iKillSpecManageService.update(killGoodsPrice);
        }

        return HttpResponseBody.successResponse("保存成功");
    }

    @PostMapping("/delete")
    public HttpResponseBody delete(Integer id){
        iKillSpecManageService.delete(id);
        return HttpResponseBody.successResponse("删除成功");
    }



}
