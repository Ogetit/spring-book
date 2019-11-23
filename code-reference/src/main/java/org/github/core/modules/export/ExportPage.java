package org.github.core.modules.export;

import java.util.Collection;

/**
 * 分页导出
 *
 * @author heer
 * @version [版本号, Aug 19, 2014]
 * @see [相关类/方法]
 * @since [GITHUB]
 */
public abstract class ExportPage {
    /**
     * 获取数据，必须实现此类
     *
     * @param param
     * @param start
     * @param count
     *
     * @return
     */
    public abstract Collection getCollection(Object param, int start, int count) throws Exception;

    /**
     * 导出时对每个对象做前期处理可以覆盖此方法
     *
     * @param o getCollection方法返回集合中的一个对象
     */
    public void beforeExport(Object o) {
    }

    /**
     * 导出完成后需要继续做的事情,在修改任务表导出状态前调用
     *
     * @param param 第一次导出时候，传入的参数
     *
     * @throws Exception 如果异常抛出会导致导出状态是失败的。但不影响前面导出的文件
     */
    public void endExport(Object param) throws Exception {

    }

}
