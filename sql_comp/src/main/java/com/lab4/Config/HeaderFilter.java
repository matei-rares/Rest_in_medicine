package com.lab4.Config;


import com.lab4.IDM.IDMServiceGrpc;
import com.lab4.IDM.IdmService;
import com.lab4.exceptions.HeaderException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@WebFilter("/*")

public class HeaderFilter implements Filter {
    String OPEN_API_URL = "/api/openapi";
    String SWAGGER_URL = "/v3/api-docs";
    String SWAGGER_URL2 = "/swagger-ui";

    private static ThreadLocal<String> currentRole = new ThreadLocal<>();
    private static ThreadLocal<String> currentSub = new ThreadLocal<>();
    public static String getCurrentRole() {
        return currentRole.get();
    }

    public static String getCurrentSub() {
        return currentSub.get();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
       /* chain.doFilter(request, response);
        return;*/

        String requestURI = ((HttpServletRequest) request).getRequestURI();
        System.out.println("Request URI:" + requestURI);
        System.out.println("Request method: " + ((HttpServletRequest) request).getMethod());
        String method = ((HttpServletRequest) request).getMethod();
        if (requestURI.equals(OPEN_API_URL) || requestURI.contains(SWAGGER_URL) || requestURI.contains(SWAGGER_URL2)) { // daca vrea openapi il las
            chain.doFilter(request, response);
            return;
        }
        if(method.equals("OPTIONS") || method.equals("HEAD")){
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            httpResponse.getWriter().write("This method is not implemented");
            return;
        }
        if (!requestURI.startsWith("/api/medical_office")) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            httpResponse.getWriter().write("This endpoint does not exist");
            return;
        }


        String contentType = ((HttpServletRequest) request).getHeader("Content-type");
        System.out.println("Content type: " + contentType);
        //content type can be null or application/json
        if (contentType == null || contentType.equals("application/json")) {
            //verify auth
            String authorizationHeader = ((HttpServletRequest) request).getHeader("Authorization");
            System.out.println("Authorization header: " + authorizationHeader);
            String authorization = isValidAuthorization(authorizationHeader);
            if (authorization == "OK") {
                System.out.println("Current role: " + currentRole.get());
                if(currentRole.get().equals("ADMIN") || currentRole.get().equals("PACIENT") || currentRole.get().equals("DOCTOR")){
                    chain.doFilter(request, response);
                    return;
                }
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("Your role is not allowed to access this resource");
                return;

            } else {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write(authorization);
            }
            return;
        }
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        httpResponse.getWriter().write("Content-type should be null or application/json");
    }



    private String checkAuthorization(String token) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        IDMServiceGrpc.IDMServiceBlockingStub idmStub = IDMServiceGrpc.newBlockingStub(channel);

        IdmService.TokenResponse reply = idmStub.authorize(IdmService.TokenRequest.newBuilder().setToken(token).build());    //sayHello(Helloworld.HelloRequest.newBuilder().setName("gRPC").build());
        String response = reply.getMessage().strip().replace("\"", "");
        currentRole.set( reply.getRole().strip().replace("\"", ""));
        currentSub.set(reply.getSub().strip().replace("\"",""));
        channel.shutdown();
        String code = response.substring(response.length() - 3);

        return code;
    }

    private String isValidAuthorization(String authorizationHeader) {

        if (authorizationHeader == null) {
            return "Authorization header missing";
        }
        DecodedJWT jwt;
        String token = "";
        try {
            token = authorizationHeader.split(" ")[1];
            jwt = JWT.decode(token);
        } catch (Exception exception) {
            System.out.println("Checking token: " + exception.getMessage());
            return "Invalid token";
        }
        if (jwt.getExpiresAt().before(new Date())) {
            return "Token expired";
        }

        if (checkAuthorization(token).equals("200")) {
            return "OK";
        } else {
            return "Invalid token";
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
