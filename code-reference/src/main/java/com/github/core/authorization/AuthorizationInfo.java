package com.github.core.authorization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限对象
 *
 * @author 章磊
 */
public class AuthorizationInfo implements Serializable{


    private static final long serialVersionUID = -4371540343398382596L;
    private String moduleNo;    //模块编号

    /**
     * 某模块的所有功能
     */
    private Map<String, Map> functionMap = new HashMap<String, Map>();

    public AuthorizationInfo(String moduleNo) {
        this.moduleNo = moduleNo;
    }

    /**
     * 保存功能
     *
     * @param functionNo   功能编号
     * @param functionInfo 功能信息
     */
    public void saveFunction(String functionNo, Map functionInfo) {
        functionMap.put(functionNo, functionInfo);
    }

    /**
     * 判断功能编号是否有权限
     *
     * @param functionNo 功能编号
     * @return
     */
    public boolean has(String functionNo) {
        if (functionMap != null) {
            return functionMap.get(functionNo) != null;
        }
        return false;
    }

    public Map getFunction(String functionNo) {
        if (functionMap != null) {
            return functionMap.get(functionNo);
        }
        return null;
    }

    public String getModuleNo() {
        return moduleNo;
    }

    public Map<String, Map> getFunctionMap() {
        return functionMap;
    }

    /**
     * 模块编号对应模块下所有功能
     */
    private Map<String, Map<String, Map>> moduleMap = new HashMap<String, Map<String, Map>>();


    public Map<String, Map<String, Map>> getModuleMap() {
        moduleMap.put(moduleNo, functionMap);
        return moduleMap;
    }

}
