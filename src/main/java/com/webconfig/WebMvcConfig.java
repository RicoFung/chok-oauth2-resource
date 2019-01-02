package com.webconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
	@Value("${static.path}")
	private String	STATIC_PATH;
	@Value("${static.doBase}")
	private String	STATIC_DOBASE;
	@Value("${jsp.static.path}")
	private String	JSP_STATIC_PATH;
	@Value("${jsp.static.doBase}")
	private String	JSP_STATIC_DOBASE;
	@Value("${spring.mvc.view.prefix}")
	private String	SPRING_MVC_VIEW_PREFIX;
	@Value("${spring.mvc.view.suffix}")
	private String	SPRING_MVC_VIEW_SUFFIX;
	@Value("${spring.mvc.view.view-name}")
	private String	SPRING_MVC_VIEW_VIEWNAME;

	/**
	 * 配置默认页
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry)
	{
        registry.addViewController("login").setViewName("html/login");
        registry.addViewController("/").setViewName("html/index");
	}

	/**
	 * 配置虚拟目录，用于非nginx环境
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		/*
		 * 说明：增加虚拟路径(经过本人测试：在此处配置的虚拟路径，用springboot内置的tomcat时有效，
		 * 用外部的tomcat也有效;所以用到外部的tomcat时不需在tomcat/config下的相应文件配置虚拟路径了,阿里云linux也没问题)
		 */
		registry.addResourceHandler(STATIC_PATH).addResourceLocations("file:" + STATIC_DOBASE);
		registry.addResourceHandler(JSP_STATIC_PATH).addResourceLocations(JSP_STATIC_DOBASE);
	}

	/**
	 * 配置多文件上传
	 * 
	 * @return CommonsMultipartResolver
	 */
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver()
	{
		CommonsMultipartResolver cmr = new CommonsMultipartResolver();
		// 默认编码
		cmr.setDefaultEncoding("UTF-8");
		// 文件大小最大值. 注意限制的不是针对单个文件，而是所有文件的容量之和
		cmr.setMaxUploadSize(10485760000l);
		// 内存中的最大值
		cmr.setMaxInMemorySize(40960);
		return cmr;
	}

	@Bean
	public ViewResolver viewResolver()
	{
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix(SPRING_MVC_VIEW_PREFIX);
		resolver.setSuffix(SPRING_MVC_VIEW_SUFFIX);
		resolver.setViewNames(SPRING_MVC_VIEW_VIEWNAME);
		resolver.setOrder(2);
		return resolver;
	}
}
