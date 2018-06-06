package space.wangjiang.summer.model.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Connection;

/**
 * 事务注解，仅支持路由方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transaction {

    /**
     * <pre>
     * 事务的级别：
     * Connection.TRANSACTION_READ_UNCOMMITTED
     * TRANSACTION_READ_COMMITTED
     * TRANSACTION_REPEATABLE_READ
     * TRANSACTION_SERIALIZABLE
     *
     * 事务导致的问题：
     * 脏读：事务A读取了事务B未提交的数据，并在这个基础上又做了其他操作
     * 不可重复读：事务A读取了事务B已提交的更改数据
     * 幻读：事务A读取了事务B已提交的新增数据
     *
     * 脏读是需要绝对避免的，后两个大多数情况不需要考虑，MySql的默认事务隔离级别就是READ_COMMITTED
     *
     * 事务隔离：
     * 事务隔离级别      脏读	 不可重复读 	幻读
     * READ_UNCOMMITTED 允许    允许     允许
     * READ_COMMITTED   禁止    允许     允许
     * REPEATABLE_READ  禁止    禁止     允许
     * SERIALIZABLE     禁止    禁止     禁止
     * </pre>
     */
    int value() default Connection.TRANSACTION_READ_COMMITTED;

}
