package com.wang.tradecenter.controller;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.ExceptionOrderDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.tradecenter.entity.BO.ExceptionOrder;
import com.wang.tradecenter.service.IExceptionOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/4/25 21:45
 * @Description:
 */

@RestController
@RequestMapping("/order")
public class ExceptionOrderController {

    @Autowired
    private IExceptionOrderService exceptionOrderService;

    @PostMapping("/ExceptionOrder")
    public Response<PageInfo<ExceptionOrderDTO>> getExceptionOrderByStoreId(@RequestBody ExceptionOrderDTO exceptionOrderDTO){
        ExceptionOrder exceptionOrder = buildBO(exceptionOrderDTO);
        PageInfo<ExceptionOrder> result = exceptionOrderService.getExceptionOrderByStoreId(exceptionOrder);
        return ResponseUtil.success(PageUtils.getPageInfo(result,result.getList().stream()
                .map(ExceptionOrder::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("dealWith")
    public Response<Integer> dealWithExceptionOrder(@RequestBody ExceptionOrderDTO exceptionOrderDTO){
        ExceptionOrder exceptionOrder = buildBO(exceptionOrderDTO);
        Integer result = exceptionOrderService.dealWithExceptionOrder(exceptionOrder);
        return ResponseUtil.success(result);
    }

    private ExceptionOrder buildBO(ExceptionOrderDTO exceptionOrderDTO) {
        return BOUtils.convert(ExceptionOrder.class, exceptionOrderDTO);
    }

}
