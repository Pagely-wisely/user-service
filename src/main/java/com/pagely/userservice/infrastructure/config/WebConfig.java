package com.pagely.userservice.infrastructure.config;

import com.pagely.userservice.infrastructure.security.CurrentUserIdResolver;
import com.pagely.userservice.infrastructure.security.CurrentUserRoleResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정.
 *
 * <p>커스텀 ArgumentResolver 등록:
 * <ul>
 *   <li>{@link CurrentUserIdResolver}</li>
 *   <li>{@link CurrentUserRoleResolver}</li>
 * </ul>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserIdResolver());
        resolvers.add(new CurrentUserRoleResolver());
    }
}
