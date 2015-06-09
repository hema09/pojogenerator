package com.crud.test;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: hbhatia
 * Date: 5/17/15
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class CORSFilter implements Filter {

    public CORSFilter() { }

    public void init(FilterConfig fConfig) throws ServletException { }

    public void destroy() {	}

    public void doFilter(
            ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        //System.out.println("cors filter added");
        ((HttpServletResponse)response).addHeader(
                "Access-Control-Allow-Origin", "*"
        );
        chain.doFilter(request, response);
    }
}
