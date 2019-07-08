package cn.enjoy.mall.web.controller;

import cn.enjoy.mall.service.IPayService;
import cn.enjoy.sys.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import cn.enjoy.core.utils.response.HttpResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PayController extends BaseController {
    @Reference
    private IPayService payService;

//    调用微信支付连接地址：传递支付编码和支付金额
//    返回支付二维码地址。
    @PostMapping("wxPrePay")
    public HttpResponseBody wxPrePay(Integer orderId,String payCode, BigDecimal payAmount){
        Map<String, String> preMap = payService.doPrePay(orderId,payCode,payAmount,getSessionUserId());
        if("success".equalsIgnoreCase(preMap.get("result_code"))){
            return HttpResponseBody.successResponse("生成预支付单成功",preMap);
        }else{
            return HttpResponseBody.failResponse(preMap.get("return_msg"));
        }
    }
    @PostMapping("checkPay")
    public HttpResponseBody checkPay(String prepayId){
        String payStatus = payService.queryByPrepayId(prepayId);
        Map<String, String> preMap = new HashMap<>();
        preMap.put("payStatus",payStatus);
        return HttpResponseBody.successResponse("支付成功",preMap);
    }
    @PostMapping("orderPay")
    public HttpResponseBody orderPay(Integer orderId,String payCode, BigDecimal payAmount){
        String payResult = payService.doPay(orderId,payCode,payAmount,getSessionUserId());
        if("success".equals(payResult)){
            return HttpResponseBody.successResponse("支付成功");
        }else{
            return HttpResponseBody.failResponse(payResult);
        }
    }
}
