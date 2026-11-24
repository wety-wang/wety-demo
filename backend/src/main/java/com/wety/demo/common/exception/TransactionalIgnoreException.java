package com.wety.demo.common.exception;

/**
 * 事物不需要回滚的异常
 * <br>
 * 使用方法：@Transactional(noRollbackFor=TransactionalIgnoreException.class)
 *
 * @author wety
 */
public class TransactionalIgnoreException extends Exception {

    public TransactionalIgnoreException(String msg) {
        super(msg);
    }
}
