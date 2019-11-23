package org.github.core.modules.scan;

import org.apache.commons.lang.StringUtils;
import org.github.core.modules.mybatis.page.AbstractPageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 扫描工具类
 * @author 章磊
 *
 */
public class ScanUtil {
	private static Logger logger= LoggerFactory.getLogger(ScanUtil.class);
	/**
	 * 基于classpath目录得到所有class对象
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Set<Class> scan(String path,Class cls) throws IOException, ClassNotFoundException{
		Set<Class> set = new HashSet<Class>();
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory =
				new CachingMetadataReaderFactory(resourcePatternResolver);
		Resource[] resources = resourcePatternResolver.getResources(path);
		for(Resource resource : resources){
			MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
			ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
			sbd.setResource(resource);
			sbd.setSource(resource);
			Class clsnew  = sbd.resolveBeanClass(ScanUtil.class.getClassLoader());
			
			if(cls==null){
				set.add(clsnew);
			}else{
				boolean isFind = false;
				if(clsnew.getInterfaces()!=null){
					for(Class interfac : clsnew.getInterfaces()){
						if(interfac.getName().equals(cls.getName())){
							isFind = true;
							break;
						}
					}
				}
				if(cls.isAssignableFrom(clsnew)){
					isFind = true;
				}
				if(isFind)
				set.add(clsnew);
			}
		}
		return set;
	}
	public static void deleteRepeatDao() throws Exception{
		String mapperPath="classpath:/cn/github/**/*dao/*Dao.class";
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		Resource[] resources = resourcePatternResolver.getResources(mapperPath);
		Map<String,Resource> resourceMap=new HashMap<String, Resource>();
		for(Resource resource : resources){
			String subfix= StringUtils.substringAfterLast(resource.getFile().getPath(),File.separator);
			subfix=StringUtils.lowerCase(subfix);
			Resource existResource=resourceMap.get(subfix);
			if(existResource==null){
				resourceMap.put(subfix,resource);
				continue;
			}
			if(existResource.getFile().lastModified()>resource.getFile().lastModified()){
				resource.getFile().delete();
				logger.error("删除掉了重名的dao:"+resource.getFile().getPath());
			}else{
				resourceMap.put(subfix,resource);
				existResource.getFile().delete();
				logger.error("删除掉了重名的dao:"+existResource.getFile().getPath());
			}
		}
	}
	/**
	 * 扫描entity
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Set<Class> myBatisScan(String path,Class cls) throws IOException, ClassNotFoundException{
		Set<Class> set = new HashSet<Class>();
		Map<String,Resource> resourceMap=new HashMap<String, Resource>();
		Map<String,Class> classMap=new HashMap<String, Class>();
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory =
				new CachingMetadataReaderFactory(resourcePatternResolver);
		Resource[] resources = resourcePatternResolver.getResources(path);
		for(Resource resource : resources){
			MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
			ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
			sbd.setResource(resource);
			sbd.setSource(resource);
			Class clsnew  = sbd.resolveBeanClass(ScanUtil.class.getClassLoader());
			if(cls!=null){
				boolean isFind = false;
				if(clsnew.getInterfaces()!=null){
					for(Class interfac : clsnew.getInterfaces()){
						if(interfac.getName().equals(cls.getName())){
							isFind = true;
							break;
						}
					}
				}
				if(cls.isAssignableFrom(clsnew)){
					isFind = true;
				}
				if(!isFind){
					continue;
				}
			}
			String beanName=clsnew.getSimpleName();
			Resource existResource=resourceMap.get(beanName);
			if(existResource==null){
				resourceMap.put(beanName,resource);
				classMap.put(beanName,clsnew);
				set.add(clsnew);
				continue;
			}
			File existBeanFile= existResource.getFile();
			File currentBeanFile=resource.getFile();
			if(existBeanFile.lastModified() > currentBeanFile.lastModified()){
				// 当前class旧了
				logger.error("删除掉了重名的entity:"+currentBeanFile.getPath());
				currentBeanFile.delete();
			}else{
				Class existBeanClass  = classMap.get(beanName);
				resourceMap.put(beanName,resource);
				classMap.put(beanName,clsnew);
				sbd.setResource(existResource);
				sbd.setSource(existResource);
				set.remove(existBeanClass);
				set.add(clsnew);
				existBeanFile.delete();
				logger.error("删除掉了重名的entity:"+existBeanFile.getPath());
			}
		}
		return set;
	}
	public static String[] findPackages(String path){
		try{
			List<String> list = new ArrayList<String>();
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			MetadataReaderFactory metadataReaderFactory =
					new CachingMetadataReaderFactory(resourcePatternResolver);
			//"classpath*:cn/github/**/entity"
			String classpath =  "classpath*:"+path;
			Resource[] resources = resourcePatternResolver.getResources(classpath);
			String reg = path.replace("**",".*");
			reg = path.replace("*",".*");
			Pattern pattern = Pattern.compile(reg);
			for(Resource resource : resources){
				Matcher matcher =pattern.matcher(resource.getURL().getPath());
				if(matcher.find()){
					list.add(matcher.group(0));
				}

			}
			return list.toArray(new String[]{});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String aa[] = ScanUtil.findPackages("cn/github/**/entity/*.class");
		//System.out.println(aa.length);
		Set a = ScanUtil.scan("cn/github/**/entity/*.class", AbstractPageEntity.class);
		//System.out.println(a.size());
		//System.out.println(a);
	}
}
