package com.intera.util.web.servlet.filter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class JsonpCallbackFilterTest {

    public static final String CALLBACK = "callback";
    @Mock
    private MockHttpServletRequest request;
    @Mock
    private MockHttpServletResponse response;
    @Mock
    private MockFilterChain chain;
    @Mock
    private Map<String, String[]> parameterMap;

    private JsonpCallbackFilter filter = new JsonpCallbackFilter();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testMissingCallback() throws IOException, ServletException {
        when(request.getParameterMap()).thenReturn(parameterMap);
        when(parameterMap.containsKey(CALLBACK)).thenReturn(false);

        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testContentWrappedInCallback() throws IOException, ServletException {
        Map map = new HashMap();
        map.put(CALLBACK, new String[] {"foo"});
        ServletOutputStream out = mock(ServletOutputStream.class);

        when(request.getParameterMap()).thenReturn(map);
        when(response.getOutputStream()).thenReturn(out);

        filter.doFilter(request, response, chain);
        verify(out).write("foo();".getBytes());

    }
}
