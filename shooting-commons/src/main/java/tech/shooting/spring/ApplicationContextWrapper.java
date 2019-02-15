package tech.shooting.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ApplicationContextWrapper {
	private static ApplicationContext ctx = null;

	@Autowired
	public ApplicationContextWrapper(ApplicationContext ac) {
		log.info("Creating Context provider bean");
		ctx = ac;
	}

	public static Object getBean(String beanName) {
		return ctx.getBean(beanName);
	}

	public static <T> T getBean(Class<T> beanClass) {
		return ctx.getBean(beanClass);
	}

	public static boolean isActivated() {
		return ctx != null;
	}
}
