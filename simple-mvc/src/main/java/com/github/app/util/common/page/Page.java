package com.github.app.util.common.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.github.app.util.json.fastjson.FastjsonUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分页查询结果
 */
@ApiModel("分页对象")
public class Page<T> implements Serializable {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 3984948268035858349L;

    /**
     * 默认每页显示记录数
     */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数")
    private Integer totalCount = 0;

    /**
     * 每页显示记录数
     */
    @ApiModelProperty(value = "每页显示记录数", example = "10")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage = 1;

    /**
     * 查询实体
     */
    @ApiModelProperty(value = "查询实体")
    private T queryEntity;

    @ApiModelProperty(value = "泛型对象类型")
    protected Class<T> entityClass;

    /**
     * 数据
     */
    @ApiModelProperty(value = "业务数据")
    private List<T> data = new ArrayList<T>();

    /**
     * 无参构造器-作为前端请求参数时spring初始化使用
     */
    public Page() {
    }

    /**
     * 构造器 包括总页数、当前页码、每页显示记录数  若每页显示记录数小于1则为默认记录数（10）
     *
     * @param totalCount  总页数
     * @param pageSize    每页显示记录
     * @param currentPage 当前页码
     */
    public Page(int totalCount, int pageSize, Integer currentPage) {
        this.setTotalCount(totalCount);
        this.setPageSize(pageSize);
        this.setCurrentPage(currentPage);
    }

    public Integer getTotalPage() {
        return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount < 1 ? 0 : totalCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public Integer getCurrentPage() {
        if (this.currentPage == null || this.currentPage < 1) {
            this.currentPage = 1;
        }
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public T getQueryEntity() {
        return queryEntity;
    }

    public void setQueryEntity(T queryEntity) {
        if (queryEntity == null) {
            return;
        }

        if (queryEntity instanceof JSONObject) {
            // 模板类有时不能自动识别，特别是接口传参Json序列化过来的类
            this.queryEntity = FastjsonUtil.fromJson(((JSONObject) queryEntity).toJSONString(), this.entityClass);
        } else {
            this.queryEntity = queryEntity;
            this.entityClass = (Class<T>) queryEntity.getClass();
        }
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        // 模板类有时不能自动识别，特别是接口传参Json序列化过来的类
        if (null != queryEntity && !queryEntity.getClass().equals(entityClass)) {
            this.queryEntity = FastjsonUtil.copy(this.queryEntity, entityClass);
        }
        this.entityClass = entityClass;
    }
}
