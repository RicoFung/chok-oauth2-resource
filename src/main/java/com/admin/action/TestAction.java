package com.admin.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAction
{
	private final static Logger log = LoggerFactory.getLogger(TestAction.class);

	@RequestMapping("/product/{id}")
	public String getProduct(@PathVariable String id)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.debug("authentication : {}", authentication);
		log.debug("product id : {}", id);
		return "product id : " + id;
	}
}
