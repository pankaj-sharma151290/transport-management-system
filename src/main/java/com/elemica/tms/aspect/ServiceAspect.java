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
public class ServiceAspect {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * This method will generate log to all service method specifying the time it
	 * took to process.
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */

	@Around("service() && allMethod()")
	public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			String className = joinPoint.getSignature().getDeclaringTypeName();
			String methodName = joinPoint.getSignature().getName();
			Object result = joinPoint.proceed();
			long elapsedTime = System.currentTimeMillis() - start;
			logger.info("Service Method {}.{} () execution time : : {} ms", className, methodName, elapsedTime);
			return result;
		} catch (IllegalArgumentException e) {
			logger.error("Illegal argument {} in {} ()", Arrays.toString(joinPoint.getArgs()),
					joinPoint.getSignature().getName() + "()");
			throw e;
		}

	}

	@Pointcut("within(@org.springframework.stereotype.Service *)")
	public void service() {
	}

	@Pointcut("execution(* *.*(..))")
	protected void allMethod() {
	}

}
