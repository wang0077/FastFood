package com.wang.accountcenter.config;

import com.wang.accountcenter.entity.BO.ExperienceLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/24 19:29
 * @Description:
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fastfood")
public class ExperienceProperties {


    private List<ExperienceLevel> levels;

}
