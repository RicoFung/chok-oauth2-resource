package com.webconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
	/**
	 * 配置默认页
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry)
	{
        registry.addViewController("/").setViewName("html/index");
	}
}
