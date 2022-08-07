package com.wang.authcenter.security.extension;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
import com.wang.authcenter.Util.JSONUtil;
import com.wang.authcenter.service.Remote.UserRemote;
import com.wang.authcenter.service.impl.UserService;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import lombok.Data;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;

/**
 * @Auther: wAnG
 * @Date: 2022/4/7 17:39
 * @Description:
 */

@Data
public class WechatAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private WxMaService wxMaService;
    @Autowired
    private UserRemote userRemote;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WechatAuthenticationToken authenticationToken = (WechatAuthenticationToken) authentication;
        String code = (String) authenticationToken.getPrincipal();

        WxMaJscode2SessionResult sessionInfo = null;
        try {
            sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        String openid = sessionInfo.getOpenid();

        Response<UserDTO> memberAuthResult = userRemote.getById(openid);
        // 微信用户不存在，注册成为新会员
        if (memberAuthResult != null && memberAuthResult.getCode() == CodeEnum.USER_NOT_EXIST.getCode()){
            String sessionKey = sessionInfo.getSessionKey();
            String encryptedData = authenticationToken.getEncryptedData();
            String iv = authenticationToken.getIv();
            String infoJson = WxMaCryptUtils.decryptAnotherWay(sessionKey, encryptedData, iv);
            WxMaUserInfo userInfo = JSONUtil.parse(infoJson, WxMaUserInfo.class);
            UserDTO userDTO = buildUserDTO(userInfo, openid);
            userRemote.UserRegister(userDTO);
        }
        UserDetails userDetails = ((UserService)userDetailsService).loadUserByOpenId(openid);
        WechatAuthenticationToken result = new WechatAuthenticationToken(userDetails, new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthenticationToken.class.isAssignableFrom(authentication);
    }


    private UserDTO buildUserDTO(WxMaUserInfo userInfo,String openId){
        UserDTO userDTO = new UserDTO();
        userDTO.setUid(openId);
        userDTO.setAvatarUrl(userInfo.getAvatarUrl());
        userDTO.setSex(Integer.parseInt(userInfo.getGender()));
        userDTO.setRole(1);
        userDTO.setUsername(userInfo.getNickName());
        return userDTO;
    }
}
