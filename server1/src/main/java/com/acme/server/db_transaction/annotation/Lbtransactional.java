package com.acme.server.db_transaction.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lbtransactional {
    // 代表属于分布式事务


    boolean isStart() default false;
    boolean isEnd() default false;
}
