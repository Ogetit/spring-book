package org.github.core.modules.export;

import org.apache.commons.lang.StringUtils;
import org.github.core.modules.exception.BusinessException;
import org.github.core.modules.exception.SystemErrorCode;

/**
 * Created by github on 2016/10/28.
 */
public class ExportParam {
    //PJ_JSF,票面_机建;PJ_TAX,票面_税费; 键值对用;分开
    private String title;//标题
    private String taskName;//导出任务名称
    private String mkbh;//模块编号
    private String pt;//ASMS/B2B/B2G 取枚举ExportPlatEnum
    private String compid;
    private String deptid;
    private String userid;//B2G平台导出传入常旅客id
    private String hyid;//B2G平台导出传入会员id

    /**
     * 如果是csv则为0或空，如果是xls则为1,如果导出的是其他文件需要自己实现导出任务 (2 为zip)
     */
    private String fileType;

    public void validParam() throws BusinessException {
        if (StringUtils.isBlank(pt)) {
            throw new BusinessException(SystemErrorCode.ParamError, "pt不能为空");
        }
        if (StringUtils.isBlank(compid)) {
            throw new BusinessException(SystemErrorCode.ParamError, "compid不能为空");
        }
        if (StringUtils.isBlank(deptid)) {
            throw new BusinessException(SystemErrorCode.ParamError, "deptid不能为空");
        }
        if (StringUtils.isBlank(userid)) {
            throw new BusinessException(SystemErrorCode.ParamError, "userid不能为空");
        }
        if (StringUtils.isBlank(mkbh)) {
            throw new BusinessException(SystemErrorCode.ParamError, "mkbh不能为空");
        }
        if (StringUtils.isBlank(taskName)) {
            throw new BusinessException(SystemErrorCode.ParamError, "taskName不能为空");
        }
        if (StringUtils.isBlank(fileType)) {
            fileType = "0";
        }

        if (!("1".equals(fileType) || "0".equals(fileType) || "2".equals(fileType))) {
            fileType = "0";
        }
        if (StringUtils.isBlank(title) && "0".equals(fileType)) {
            throw new BusinessException(SystemErrorCode.ParamError, "title不能为空");
        }
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
