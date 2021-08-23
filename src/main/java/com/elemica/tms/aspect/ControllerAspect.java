package com.elemica.tms.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * This method will generate log to all controller method specifying the time it
	 * took to process.
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("controller() && allMethod()")
	public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			String className = joinPoint.getSignature().getDeclaringTypeName();
			String methodName = joinPoint.getSignature().getName();
			Object result = joinPoint.proceed();
			long elapsedTime = System.currentTimeMillis() - start;
			logger.info("Controller Method {}.{} () execution time : : {} ms", className, methodName, elapsedTime);
			return result;
		} catch (IllegalArgumentException e) {
			logger.error("Illegal argument {} in {} ()", Arrays.toString(joinPoint.getArgs()),
					joinPoint.getSignature().getName() + "()");
			throw e;
		}

	}

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void controller() {
	}

	@Pointcut("execution(* *.*(..))")
	protected void allMethod() {
	}

}
