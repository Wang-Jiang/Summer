package space.wangjiang.summer.model.transaction;

/**
 * 事务的原子操作
 */
public interface Atom {

    boolean run() throws Exception;

}
