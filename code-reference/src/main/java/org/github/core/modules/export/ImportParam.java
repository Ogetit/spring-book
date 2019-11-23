package org.github.core.modules.export;

import org.apache.commons.lang.StringUtils;
import org.github.core.modules.exception.BusinessException;
import org.github.core.modules.exception.SystemErrorCode;

/**
 * Created by github on 2016/12/26.
 */
public class ImportParam {
    private String taskName;//导出任务名称
    private String mkbh;//模块编号
    private String pt;//ASMS/B2B/B2G 取枚举ExportPlatEnum
    private String compid;
    private String deptid;
    private String userid;//B2G平台导入传入常旅客id
    private String hyid;//B2G平台导入传入会员id
    private String filePath;//导入文件的路径
    public void validParam() throws BusinessException {
        if(StringUtils.isBlank(filePath)){
            throw new BusinessException(SystemErrorCode.ParamError,"filePath不能为空");
        }
        if(StringUtils.isBlank(pt)){
            throw new BusinessException(SystemErrorCode.ParamError,"pt不能为空");
        }
        if(StringUtils.isBlank(compid)){
            throw new BusinessException(SystemErrorCode.ParamError,"compid不能为空");
        }
        if(StringUtils.isBlank(deptid)){
            throw new BusinessException(SystemErrorCode.ParamError,"deptid不能为空");
        }
        if(StringUtils.isBlank(userid)){
            throw new BusinessException(SystemErrorCode.ParamError,"userid不能为空");
        }
        if(StringUtils.isBlank(mkbh)){
            throw new BusinessException(SystemErrorCode.ParamError,"mkbh不能为空");
        }
        if(StringUtils.isBlank(taskName)){
            throw new BusinessException(SystemErrorCode.ParamError,"taskName不能为空");
        }
    }
    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public String getCompid() {
        return compid;
    }

    public void setCompid(String compid) {
        this.compid = compid;
    }

    public String getDeptid() {
        return deptid;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getHyid() {
        return hyid;
    }

    public void setHyid(String hyid) {
        this.hyid = hyid;
    }

    public String getMkbh() {
        return mkbh;
    }

    public void setMkbh(String mkbh) {
        this.mkbh = mkbh;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
