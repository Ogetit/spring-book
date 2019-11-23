package org.github.core.modules.excel;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果导出需要用到自定义fn函数，则在此类中增加
 * Created by github on 2016/10/30.
 */
public class ExportFunctions {
    /**
     * 更具枚举对获取编号对应的中文名
     * @param code//code
     * @param enums//枚举的键值对格式如'P:个人会员,E:机构会员'
     * @param defaultValue//默认值
     * @return
     * @throws Exception
     */
    public static String getNameByCode(String code,String enums,String... defaultValue) throws Exception {
        if(StringUtils.isBlank(enums)){
            throw new Exception("枚举键值对不能为空");
        }
        Map<String,String> enumMap=new HashMap<String, String>();
        String[] enumArr=enums.split(",");
        boolean hasNull=false;
        for(String oneEnum : enumArr){
            String[] keyValue=oneEnum.split(":");
            if(keyValue.length!=2){
                throw new Exception("枚举键值对传入格式错误");
            }
            if("null".equals(keyValue[0])){
                hasNull=true;
            }
            enumMap.put(keyValue[0],keyValue[1]);
        }
        if(hasNull){
            code=code==null ? "null" : code;
        }
        if(enumMap.get(code)==null && defaultValue!=null && defaultValue.length>0){
            return defaultValue[0];
        }
        return enumMap.get(code);
    }
}
