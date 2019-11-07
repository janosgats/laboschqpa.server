package com.labosch.csillagtura.advice;

import com.google.gson.JsonObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

/**
 * This annotation to makes methods DB transactional even if you call them from the same class.
 */

@Aspect
@Component
public class SameClassTransactionalAdvice {
    private final Logger logger = LoggerFactory.getLogger(SameClassTransactionalAdvice.class);

    @Autowired(required = true)
    TransactionTemplate transactionTemplate;

    @Around("@annotation(com.labosch.csillagtura.advice.SameClassTransactionalString)")
    public String sameClassTransactionalAdviceString(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("In around advice: sameClassTransactionalAdviceString");

        ThrowableContainer throwableThrownInTransactionCallback_Container = new ThrowableContainer();
        String returned = transactionTemplate.execute(new TransactionCallback<String>() {

            @Override
            public String doInTransaction(TransactionStatus transactionStatus) {
                try {
                    return (String) joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwableThrownInTransactionCallback_Container.setThrowable(throwable);
                }
                return null;
            }

        });

        if (throwableThrownInTransactionCallback_Container.getThrowable() != null)
            throw throwableThrownInTransactionCallback_Container.getThrowable();

        return returned;
    }

    @Around("@annotation(com.labosch.csillagtura.advice.SameClassTransactionalBoolean)")
    public Boolean sameClassTransactionalAdviceBoolean(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("In around advice: sameClassTransactionalAdviceBoolean");

        ThrowableContainer throwableThrownInTransactionCallback_Container = new ThrowableContainer();
        Boolean returned = transactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    return (Boolean) joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwableThrownInTransactionCallback_Container.setThrowable(throwable);
                }
                return null;
            }

        });

        if (throwableThrownInTransactionCallback_Container.getThrowable() != null)
            throw throwableThrownInTransactionCallback_Container.getThrowable();

        return returned;
    }

    @Around("@annotation(com.labosch.csillagtura.advice.SameClassTransactionalVoid)")
    public void sameClassTransactionalAdviceVoid(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("In around advice: sameClassTransactionalAdviceVoid");

        ThrowableContainer throwableThrownInTransactionCallback_Container = new ThrowableContainer();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            public void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwableThrownInTransactionCallback_Container.setThrowable(throwable);
                }
            }

        });

        if (throwableThrownInTransactionCallback_Container.getThrowable() != null)
            throw throwableThrownInTransactionCallback_Container.getThrowable();
    }
}
