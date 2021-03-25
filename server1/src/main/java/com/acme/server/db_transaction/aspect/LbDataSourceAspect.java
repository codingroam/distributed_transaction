package com.acme.server.db_transaction.aspect;

import com.acme.server.db_transaction.connection.LbConnection;
import com.acme.server.db_transaction.transactional.LbTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Aspect
@Component
public class LbDataSourceAspect {

    /**
     * 切的是一个接口，所以所有的实现类都会被切到
     * spring肯定会调用这个方法来生成一个本地事务
     * 所以point.proceed()返回的也是一个Connection
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable {
        if (LbTransactionManager.getCurrent() != null) {
            return new LbConnection((Connection) point.proceed(), LbTransactionManager.getCurrent());
        } else {
            return (Connection) point.proceed();
        }
    }
}
