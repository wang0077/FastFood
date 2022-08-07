package com.wang.accountcenter.controller;

import com.wang.accountcenter.config.ExperienceProperties;
import com.wang.accountcenter.entity.BO.UserAccount;
import com.wang.accountcenter.entity.UserAmountRequest;
import com.wang.accountcenter.rechargeAmountRequest;
import com.wang.accountcenter.service.IUserAccountService;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.Util.SqlResultUtil;
import com.wang.fastfood.apicommons.entity.DTO.UserAccountDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.request.CalculateInterestRequest;
import com.wang.fastfood.apicommons.entity.request.PayRequest;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 17:44
 * @Description:
 */

@RestController
@RequestMapping("Account/")
public class UserAccountController {

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private ExperienceProperties properties;

    @PostMapping("/insert")
    public Response<String> Insert(@RequestBody UserAccountDTO userAccountDTO){
        UserAccount userAccount = buildBO(userAccountDTO);
        int result = userAccountService.insert(userAccount);
        return SqlResultUtil.insertResult(result);
    }

    @PostMapping("/getByAccountInfo")
    public Response<UserAccountDTO> getByUserId(@RequestBody UserAccountDTO userAccountDTO){
        UserAccount userAccount = buildBO(userAccountDTO);
        UserAccount result = userAccountService.getByUserId(userAccount);
        return result == null ? ResponseUtil.success(CodeEnum.SUCCESS) : ResponseUtil.success(result.doBackward());
    }

    @PostMapping("/getUserAmount")
    public Response<Double> getUserAmount(@RequestBody UserAmountRequest request){
        String openId = request.getUid();
        Double amount = userAccountService.getUserAmount(openId);
        return ResponseUtil.success(amount);
    }

    @PostMapping("/rechargeAmount")
    public Response rechargeAmount(@RequestBody rechargeAmountRequest request){
        UserAccount userAccount = buildUserAccount(request);
        userAccountService.rechargeAmount(userAccount);
        return ResponseUtil.success();
    }

    @PostMapping("/pay")
    public Response pay(@RequestBody PayRequest request){
        String openId = request.getUid();
        Double amount = request.getAmount();
        userAccountService.pay(openId,amount);
        return ResponseUtil.success();
    }

    @PostMapping("/calculate")
    public Response<String> calculateOrderInterest(@RequestBody CalculateInterestRequest request){
        Double amount = request.getAmount();
        String openId = request.getUid();
        Integer result = userAccountService.calculateOrderInterest(openId, amount);
        return SqlResultUtil.updateResult(result);
    }

    private UserAccount buildBO(UserAccountDTO userAccountDTO) {
        return BOUtils.convert(UserAccount.class, userAccountDTO);
    }

    private UserAccount buildUserAccount(rechargeAmountRequest request){
        UserAccount userAccount = new UserAccount();
        userAccount.setUid(request.getOpenId());
        userAccount.setAmount(request.getAmount());
        return userAccount;
    }

}
