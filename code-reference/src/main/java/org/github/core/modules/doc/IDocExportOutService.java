package org.github.core.modules.doc;

import org.github.core.modules.exception.BusinessException;

import java.util.Map;

/**
 * Created by github on 2017/1/18.
 * 获取Docx模板数据
 */
public interface IDocExportOutService {

    /**
     * 返回的map:
     * key:"beanname"
     * value: baen  bean结构 {name,id,xb,map}
     * <p>
     * key:"beanList"
     * value: List<bean>
     * <p>
     * key:"mapname"
     * value: HashMap<String,Object>
     *
     * @param request 传入给业务方用来获取数据的参数，如果是controller接入的，则会把调用controller时传入的参数原样的传入
     * @return 业务方按照doc模板设置数据表达式，把数据存入map返回。
     */
    Map loadData(Map request) throws BusinessException;
}
