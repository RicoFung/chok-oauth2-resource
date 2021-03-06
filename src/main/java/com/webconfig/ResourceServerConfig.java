package com.webconfig;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter
{
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	Environment env;
	
	private TokenExtractor tokenExtractor = new BearerTokenExtractor();
	
	@Override
	public void configure(HttpSecurity http) throws Exception
	{
		http.addFilterAfter(new OncePerRequestFilter()
		{
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException
			{
				// We don't want to allow access to a resource with no token so clear
				// the security context in case it is actually an OAuth2Authentication
				if (tokenExtractor.extract(request) == null)
				{
					SecurityContextHolder.clearContext();
				}
				filterChain.doFilter(request, response);
			}
		}, AbstractPreAuthenticatedProcessingFilter.class);

		http
		// Since we want the protected resources to be accessible in the UI as well we
		// need
		// session creation to be allowed (it's disabled by default in 2.0.6)
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and().requestMatchers()
		.anyRequest().and().anonymous().and().authorizeRequests()
		// .antMatchers("/order/**").access("#oauth2.hasScope('select') and hasRole('ROLE_USER')")
		.antMatchers("/product/**").authenticated();// 配置product访问控制，必须认证过后才可以访问
	}

	@Bean
	public ResourceServerTokenServices tokenServices()
	{
		RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		remoteTokenServices.setCheckTokenEndpointUrl(env.getProperty("security.oauth2.authorization.check-token-access"));
		remoteTokenServices.setClientId(env.getProperty("security.oauth2.client.client-id"));
		remoteTokenServices.setClientSecret(env.getProperty("security.oauth2.client.client-secret"));
		return remoteTokenServices;
	}
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception
	{
		resources.authenticationEntryPoint(new MyAuthenticationEntryPoint()).accessDeniedHandler(new MyAccessDeniedHandler());
	}

	/**
	 * 自定义Token异常信息 用于token校验失败返回信息401
	 * 
	 * @author zhuojun.feng
	 *
	 */
	private class MyAuthenticationEntryPoint implements AuthenticationEntryPoint
	{
		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authException) throws IOException, ServletException
		{
			Map<String, Object> map = new HashMap<String, Object>();
			Throwable cause = authException.getCause();
			if (cause instanceof InvalidTokenException)
			{
				map.put("code", HttpServletResponse.SC_UNAUTHORIZED);// 401
				map.put("msg", "无效的token");
			}
			else
			{
				map.put("code", "401");// 401
				map.put("msg", "访问此资源需要完全的身份验证");
			}
			map.put("data", authException.getMessage());
			map.put("success", false);
			map.put("path", request.getServletPath());
			map.put("timestamp", String.valueOf(new Date().getTime()));
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			try
			{
				ObjectMapper mapper = new ObjectMapper();
				mapper.writeValue(response.getOutputStream(), map);
			}
			catch (Exception e)
			{
				throw new ServletException();
			}
		}
	}
	
	/**
	 * 自定义403
	 * @author zhuojun.feng
	 *
	 */
	private class MyAccessDeniedHandler implements AccessDeniedHandler
	{
		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response,
				AccessDeniedException accessDeniedException) throws IOException, ServletException
		{
			response.setContentType("application/json;charset=UTF-8");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", HttpServletResponse.SC_FORBIDDEN);// 403
			map.put("msg", "权限不足");
			map.put("data", accessDeniedException.getMessage());
			map.put("success", false);
			map.put("path", request.getServletPath());
			map.put("timestamp", String.valueOf(new Date().getTime()));
			ObjectMapper mapper = new ObjectMapper();
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().write(mapper.writeValueAsString(map));
		}
	}
}
