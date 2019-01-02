package com.webconfig;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;

@Configuration
public class DruidStatViewConfig
{
	@Bean
	public ServletRegistrationBean<StatViewServlet> druidStatView()
	{
		ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<StatViewServlet>();
		registrationBean.setServlet(new StatViewServlet());
		registrationBean.addInitParameter("resetEnable", "true"); // 允许清空统计数据
//		registrationBean.addInitParameter("allow", "10.12.42.61"); // 白名单
//		registrationBean.addInitParameter("deny", "192.168.1.252"); // 黑名单
		registrationBean.addInitParameter("loginUsername", "druid"); // 用户名
		registrationBean.addInitParameter("loginPassword", "druid"); // 密码
		List<String> urlMappings = new ArrayList<String>();
		urlMappings.add("/druid/*");//// 访问，可以添加多个
		registrationBean.setUrlMappings(urlMappings);
		return registrationBean;
	}
}
