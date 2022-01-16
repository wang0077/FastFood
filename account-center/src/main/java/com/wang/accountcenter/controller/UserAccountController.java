package com.wang.accountcenter.controller;

import com.wang.accountcenter.entity.BO.UserAccount;
import com.wang.accountcenter.service.IUserAccountService;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.Util.SqlResultUtil;
import com.wang.fastfood.apicommons.entity.DTO.SignInDTO;
import com.wang.fastfood.apicommons.entity.DTO.UserAccountDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
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

    @PostMapping("/insert")
    public Response<String> Insert(@RequestBody UserAccountDTO userAccountDTO){
        UserAccount userAccount = buildBO(userAccountDTO);
        int result = userAccountService.insert(userAccount);
        return SqlResultUtil.insertResult(result);
    }

    @PostMapping("/getByUserId")
    public Response<UserAccountDTO> getByUserId(@RequestBody UserAccountDTO userAccountDTO){
        UserAccount userAccount = buildBO(userAccountDTO);
        UserAccount result = userAccountService.getByUserId(userAccount);
        return result == null ? ResponseUtil.success(CodeEnum.SUCCESS) : ResponseUtil.success(result.doBackward());
    }

    @PostMapping("/checkIn")
    public Response<SignInDTO> checkIn(@RequestBody UserAccountDTO userAccountDTO){
        return null;
    }


    private UserAccount buildBO(UserAccountDTO userAccountDTO) {
        return BOUtils.convert(UserAccount.class, userAccountDTO);
    }

}
