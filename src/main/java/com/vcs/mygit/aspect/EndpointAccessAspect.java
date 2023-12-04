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

    @Around("@annotation(com.vcs.mygit.annotation.RepositoryOwnerAccess) && args(userId, ..)")
    public Object checkOwnerAccessForUpload(ProceedingJoinPoint joinPoint, String userId) throws Throwable {
        HttpServletRequest request = getRequestFromMethodArguments(joinPoint.getArgs());

        String token = jwtService.extractTokenFromRequest(request);
        String username = jwtService.extractUserName(token);
        if (!username.equals(userId)) {
            throw new AccessDeniedException("Access is denied");
        }
        return joinPoint.proceed();
    }
    private HttpServletRequest getRequestFromMethodArguments(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                return (HttpServletRequest) arg;
            }
        }
        throw new IllegalArgumentException("HttpServletRequest argument not found");
    }
}
