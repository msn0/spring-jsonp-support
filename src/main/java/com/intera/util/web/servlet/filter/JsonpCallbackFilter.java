package com.intera.util.web.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class JsonpCallbackFilter extends GenericFilterBean {

    private static Logger log = LoggerFactory.getLogger(JsonpCallbackFilter.class);
    private static final String CONTENT_TYPE = "text/javascript;charset=UTF-8";
    private static final String CALLBACK = "callback";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final Map<String, String[]> params = httpRequest.getParameterMap();

        if (params.containsKey(CALLBACK)) {
            final String callbackName = params.get(CALLBACK)[0];
            log.debug(String.format("Wrapping response with JSONP callback %s", callbackName));

            GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);
            chain.doFilter(request, wrapper);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(String.format("%s(", callbackName).getBytes());
            outputStream.write(wrapper.getData());
            outputStream.write(String.format(");").getBytes());

            byte jsonpResponse[] = outputStream.toByteArray();
            wrapper.setContentType(CONTENT_TYPE);
            wrapper.setContentLength(jsonpResponse.length);

            OutputStream out = httpResponse.getOutputStream();
            out.write(jsonpResponse);
            out.close();

        } else {
            chain.doFilter(request, response);
        }
    }
}