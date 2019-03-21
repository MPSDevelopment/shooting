package tech.shooting.ipsc.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle (HttpServletRequest httpServletRequest, HttpServletResponse resp, AccessDeniedException e) throws IOException, ServletException {
		if(!resp.isCommitted()) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
}
