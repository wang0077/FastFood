package com.wang.tradecenter.config;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Auther: wAnG
 * @Date: 2021/11/26 20:34
 * @Description: OpenFeign自定义日志
 */
@Component
@Slf4j
public class RemoteLogger extends Logger {
    @Override
    protected void log(String configKey, String format, Object... args) {

    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        if (response.body() != null) {
            String result="";
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            int bodyLength = bodyData.length;
            if (bodyLength > 0) {
                result = Util.decodeOrDefault(bodyData, Util.UTF_8, "Binary data");
            }
            Response build = response.toBuilder().body(bodyData).build();
            Request request = build.request();
            String bodyText =  request.charset() != null ? new String(request.body(), request.charset()) : null;

            log.info("[OpenFeign_Request] ==> UseTime : [{}ms] | state : [{}] | RequestIP : [{}] | RequestURIL : [{}] |" +
                            "\n ResponseData : [{}]"
                    ,elapsedTime
                    ,response.status()
                    ,request.url().split("/")[2]
                    ,request.url()
                    ,result
            );
            return build;
        }
        return response;
    }


}
