package org.github.core.modules.mybatis;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.github.core.modules.scan.ScanUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 重载buildSqlSessionFactory,
 * 将加载mapper.xml时遇到的异常及时打到控制台
 * 
 * @author  win7
 * @version  [版本号, 2016-8-4]
 * @see  [相关类/方法]
 * @since  [GITHUB]
 */
public class ExtSqlSessionFactoryBean extends SqlSessionFactoryBean{
	Logger logger = LoggerFactory.getLogger(ExtSqlSessionFactoryBean.class);
	private Class<?> typeAliasesSuperType;
	private String typeAliasesPackage;
	
	@Override
	public void setMapperLocations(Resource[] mapperLocations) {
		if(mapperLocations==null || mapperLocations.length==0) {
			super.setMapperLocations(mapperLocations);
			return;
		}
		try {
			long ks = System.currentTimeMillis();
			super.setMapperLocations(delteReatMapper(mapperLocations));
			logger.info("删除重名Mapper耗时:"+(System.currentTimeMillis()-ks));
		} catch (Exception e) {
			logger.error("删除重名的Mapper异常",e);
		}
	}
	@Override
	public void setTypeAliasesPackage(String typeAliasesPackage) {
		this.typeAliasesPackage = typeAliasesPackage;
	}

	@Override
	public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
		this.typeAliasesSuperType = typeAliasesSuperType;
	}

	@Override
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
		long ks = System.currentTimeMillis();
		try {
			/**
			 * mybatis不是精确扫描得到entity
			 * 下面是自己实现的一个精确扫描
			 * typeAliasesSuperType和typeAliasesPackage覆盖了父类属性
			 */
			ScanUtil.deleteRepeatDao();
			long t2=System.currentTimeMillis();
			logger.info("删除重名dao耗时:"+(t2-ks));
			ks=t2;
			if(StringUtils.isNotBlank(typeAliasesPackage)) {
				Set<Class> set = ScanUtil.myBatisScan(this.typeAliasesPackage, this.typeAliasesSuperType);
				logger.info("mybatis扫描entity目录下继承AbstractPageEntity的对象个数" + set.size() + "耗时:" + (System.currentTimeMillis() - ks));
				ks = System.currentTimeMillis();
				super.setTypeAliases(set.toArray(new Class[]{}));
			}
			return super.buildSqlSessionFactory();
		} catch (Throwable e) {
			logger.error("mybatis加载错误，很严重",e);//将加载mapper.xml时遇到的异常及时打到控制台,否则会到服务器启动超时才打出错误
			throw new IOException(e);
		}finally{
			logger.info("mybatis启动耗时:"+(System.currentTimeMillis()-ks));
		}
		
	}
	private Resource[] delteReatMapper(Resource[] resources) throws Exception{
		Map<String,Resource> resourceMap=new HashMap<String, Resource>();
		List<Resource> resourceList=new ArrayList<Resource>();
		for(Resource resource : resources){
			File resourceFile;
			URL resourceUrl = resource.getURL();
			String subfix;
			//针对jar包 和 目录 中的两类映射文件分别处理
			if (ResourceUtils.isJarURL(resourceUrl)) {
				String jarFullFileName = resourceUrl.getFile();
				subfix = StringUtils.substringAfterLast(jarFullFileName, "/");
				resourceFile = ResourceUtils.getFile(ResourceUtils.extractJarFileURL(resourceUrl));
			} else {
				resourceFile = resource.getFile();
				subfix = resource.getFilename();
			}
			//依据文件名判断是否存在老的resource
			subfix=StringUtils.lowerCase(subfix);
			Resource existResource = resourceMap.get(subfix);
			if(existResource==null){
				resourceMap.put(subfix,resource);
				resourceList.add(resource);
				continue;
			}
			//处理重复resource
			File existResourceFile;
			URL existResourceUrl = existResource.getURL();
			if (ResourceUtils.isJarURL(existResourceUrl)) {
				existResourceFile = ResourceUtils.getFile(ResourceUtils.extractJarFileURL(existResourceUrl));
			} else {
				existResourceFile = existResource.getFile();
			}
			if(existResourceFile.lastModified() > resourceFile.lastModified()){
				resourceFile.delete();
				logger.error("删除掉了重名的Mapper:"+resourceFile.getPath());
			}else{
				existResourceFile.delete();
				resourceList.remove(existResource);
				resourceMap.put(subfix,resource);
				resourceList.add(resource);
				logger.error("删除掉了重名的Mapper:"+existResource.getFile().getPath());
			}
		}
		return resourceList.toArray(new Resource[resourceList.size()]);
	}
}
