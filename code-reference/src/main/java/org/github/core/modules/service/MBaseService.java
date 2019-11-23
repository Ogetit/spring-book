package org.github.core.modules.service;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.github.core.modules.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.github.core.modules.mybatis.mapper.Mapper;
import org.github.core.modules.mybatis.helper.EntityHelper;
import org.github.core.modules.mybatis.helper.EntityHelper.EntityColumn;
import org.github.core.modules.mybatis.page.AbstractPageEntity;
import org.github.core.modules.mybatis.page.PageHelper;
import org.github.core.modules.mybatis.page.PageNew;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
/**
 * Mybaits基础service
 * @author 章磊
 *
 * @param <M> Mybatis的dao
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class MBaseService<T extends AbstractPageEntity, M extends Mapper<T>> {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected Class<T> entityClass;
	public MBaseService(){
		if (this.entityClass == null) {
			this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		}
	}
	@Autowired
	private M myBatisDao;
	public M getMyBatisDao() {
		return myBatisDao;
	}

	public void setMyBatisDao(M myBatisDao) {
		this.myBatisDao = myBatisDao;
	}
	/**
	 * 根据id删除
	 * @param t
	 */
	public int deleteById(T t){
		return this.myBatisDao.deleteByPrimaryKey(t);
	}
	public int delete(T t){
		if (t == null) {
			throw new NullPointerException("删除入参不能为空");
		}
		return this.myBatisDao.delete(t);
	}
	public int insert(T t) throws Exception{
		genId(t);
		return this.myBatisDao.insert(t);
	}

	public int insertSelective(T t) throws Exception{
		genId(t);
		return this.myBatisDao.insertSelective(t);
	}

	private void genId(T t) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Set<EntityColumn> entityColumns = EntityHelper.getPKColumns(this.entityClass);
		Iterator<EntityColumn> it = entityColumns.iterator();
		EntityColumn entityColumn = null;
		while (it.hasNext()) {
			entityColumn = it.next();
			if(entityColumn.isId() && !entityColumn.isBusinessid()){
				break;
			}
		}
		//当前台传入了ID,如果有值,直接insert
		String id = BeanUtils.getProperty(t, entityColumn.getProperty());
		if(StringUtils.isBlank(id)){
			if(entityColumn.isUuid()){
				BeanUtils.setProperty(t, entityColumn.getProperty(), UUID.randomUUID().toString().replaceAll("-", ""));
			}else if("NO".equals(StringUtils.upperCase(entityColumn.getGenerator()))){
				BeanUtils.setProperty(t, entityColumn.getProperty(), DateUtil.getNo(6));
			}
		}
	}
	public int update(T t) throws Exception{
		return this.myBatisDao.updateByPrimaryKey(t);
	}
	public int updateSelective(T t)throws Exception{
		return this.myBatisDao.updateByPrimaryKeySelective(t);
	}
	public List<T> queryList(T t)throws Exception{
		return this.myBatisDao.select(t);
	}
	/**
	 * 根据id获得一个实体类
	 * @param t
	 * @return
	 */
	public T getEntityById(T t){
		return myBatisDao.selectByPrimaryKey(t);
	}
	public PageNew queryPage(Map param, int start, int count){
		PageNew page =new PageNew(start,count);
		int totalCount = myBatisDao.countDynamicSQL(entityClass, param);
		page.setTotalCount(totalCount);
		if(totalCount==0){
			return page;
		}
		PageHelper.start(page);
		List list = this.selectDynamicSQL(param);
		page.setList(list);
		return page;
	}
	public List selectDynamicSQL(Map param){
		List list = myBatisDao.selectDynamicSQL(entityClass, param);
		return EntityHelper.maplist2BeanList(list, entityClass);
	}
}
