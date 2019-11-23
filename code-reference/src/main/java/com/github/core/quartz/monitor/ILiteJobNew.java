package com.github.core.quartz.monitor;

import com.github.core.quartz.monitor.entity.LiteJob;

/**
 * job必须实现这个类
 *
 * @author java
 */
public interface ILiteJobNew {
    /**
     * 如果是要删除的job，则返回-1
     *
     * @param liteJob
     * @return
     */
    int execute(LiteJob liteJob);
}
