package com.vcs.mygit.aspect;

import com.vcs.mygit.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
public class EndpointAccessAspect {
    private final JwtService jwtService;
    @Around(value = "@annotation(com.vcs.mygit.annotation.RepositoryOwnerAccess) && args(userId, request, ..)",
            argNames = "joinPoint,userId,request")
    public Object checkOwnerAccessForUpload(
            ProceedingJoinPoint joinPoint,
            String userId,
            HttpServletRequest request
    ) throws Throwable {
        String token = jwtService.extractTokenFromRequest(request);
        if (jwtService.isTokenExpired(token))
            throw new AccessDeniedException("Token expired");

        String username = jwtService.extractUserName(token);
        if (!username.equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        return joinPoint.proceed();
    }
}
