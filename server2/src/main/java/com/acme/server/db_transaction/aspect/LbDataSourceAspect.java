package com.acme.server.db_transaction.aspect;

import com.acme.server.db_transaction.transactional.LbTransactionManager;
import com.acme.server.db_transaction.connection.LbConnection;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Aspect
@Component
public class LbDataSourceAspect {

    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable {
        if (LbTransactionManager.getCurrent() != null) {
            return new LbConnection((Connection) point.proceed(), LbTransactionManager.getCurrent());
        } else {
            return (Connection) point.proceed();
        }
    }
}
