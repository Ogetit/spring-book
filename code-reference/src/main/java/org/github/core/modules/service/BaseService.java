package org.github.core.modules.service;

import java.util.List;
import java.util.Map;

import org.github.core.modules.exception.BusinessException;
import org.github.core.modules.mybatis.mapper.Mapper;
import org.github.core.modules.mybatis.page.AbstractPageEntity;
import org.github.core.modules.mybatis.page.PageNew;

/**
 * 覆盖父类有异常的方法统一抛BusinessException
 *
 * @param <T>
 * @param <M>
 *
 * @author 章磊
 */
public abstract class BaseService<T extends AbstractPageEntity, M extends Mapper<T>> extends MBaseService<T, M> {

    @Override
    public int insert(T t) throws BusinessException {
        try {
            int count = super.insert(t);
            return count;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public int insertSelective(T t) throws BusinessException {
        try {
            int count = super.insertSelective(t);
            return count;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public int deleteById(T t) {
        int count = super.deleteById(t);
        return count;
    }

    @Override
    public int update(T t) throws BusinessException {
        try {
            int count = super.update(t);
            return count;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public int updateSelective(T t) throws BusinessException {
        try {
            int count = super.updateSelective(t);
            return count;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public List<T> queryList(T t) throws BusinessException {
        try {
            return super.queryList(t);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public T getEntityById(T t) {
        return super.getEntityById(t);
    }

    @Override
    public PageNew queryPage(Map param, int start, int count) {
        return super.queryPage(param, start, count);
    }

    @Override
    public List<?> selectDynamicSQL(Map param) {
        return super.selectDynamicSQL(param);
    }
}
