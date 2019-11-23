package org.github.core.modules.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.github.core.modules.mybatis.entity.AsmsField;
import org.github.core.modules.mybatis.page.AbstractPageEntity;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;


/**
 * 政策日志工具
 * @author zhangxy
 */
public class TransactionLogUtil {

	private static final String[] SPLITS = {",","/"};

	/**
	 * 通过map给字段赋值
	 */
	public static void setProperties(AbstractPageEntity bean, Map<String,Object> requestMap){
		if(MapUtils.isNotEmpty(requestMap)){
			Iterator<String>it = requestMap.keySet().iterator();
			while(it.hasNext()){
				String name = it.next();
				String value = ObjectUtils.toString(requestMap.get(name));
				if(StringUtils.isBlank(value)){
					continue;
				}
				//转小写,不支持驼峰命名
				name = name.toLowerCase();
				Field field = getField(name, bean.getClass());
				if(field==null){
					continue;
				}
				Object valueObj = null;
				String typeName = getTypeName(field);
				if("String".equals(typeName)){
					valueObj = value;
				}else if("Double".equals(typeName)){
					valueObj = NumberUtils.toDouble(value);
				}else if("Integer".equals(typeName)){
					valueObj = (int)NumberUtils.toDouble(value);
				}else if("Date".equals(typeName)){
					valueObj = DateUtil.strToDate(value);
				}
				if(valueObj!=null){
					setValue(name, bean, valueObj);
				}
			}
		}




	}

	private static String getTypeName(Field field) {
		String typeName = field.getType().getName();
		typeName = typeName.substring(typeName.lastIndexOf(".")+1);
		return typeName;
	}

	public static String getCzrz(AbstractPageEntity target,AbstractPageEntity db){
		return getCzrz(target,db,true);
	}


	/**
	 * 日志明细
	 * @param target 记录日志目标
	 * @param db	 数据库数据
	 * @param isall 是不是全bean比较
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getCzrz(AbstractPageEntity target,AbstractPageEntity db,boolean isall){
		StringBuffer sb = new StringBuffer();
		if(target == null || db == null){
			return "";
		}
		Field[] fields = getDeclaredFields(db.getClass());
		int index = 1;
		for(Field f : fields){
			String name = f.getName();
			AsmsField vf = f.getAnnotation(AsmsField.class);
			if(vf == null){
				continue;
			}
			Object db_value = getValue(name, db);
			Object targ_value = getValue(name, target);
			//为null的不更新
			if (!isall && targ_value == null) {
				continue;
			}
			String type = f.getType().getName();
			if(db_value instanceof String){
				if((StringUtils.isBlank((String)db_value)&&StringUtils.isNotBlank((String)targ_value)) ||
						(StringUtils.isNotBlank((String)db_value) && StringUtils.isBlank((String)targ_value))||
						!ObjectUtils.toString(db_value).equals((String)targ_value)){
					log(index,vf,(String)db_value,(String)targ_value,sb);
					index++;
				}
			}else if(db_value instanceof Integer){
				if(toInt((Integer)db_value) != toInt((Integer)targ_value )){
					log(index,vf,ObjectUtils.toString(db_value),ObjectUtils.toString(targ_value),sb);
					index++;
				}
			}else if(db_value instanceof Double){
				if(toDouble((Double)db_value) != toDouble((Double)targ_value )){
					log(index,vf,ObjectUtils.toString(db_value),ObjectUtils.toString(targ_value),sb);
					index++;
				}
			}else if(db_value instanceof Long){
				if(toLong((Long)db_value) != toLong((Long)targ_value )){
					log(index,vf,ObjectUtils.toString(db_value),ObjectUtils.toString(targ_value),sb);
					index++;
				}

			}else if(db_value instanceof Date){
				if((db_value==null && targ_value!=null) || (db_value!=null && targ_value==null)
						|| (db_value!=null && targ_value!=null && ((Date) db_value).compareTo((Date) targ_value) != 0)){
					log(index,vf, DateUtil.dateToStrLong((Date)db_value), DateUtil.dateToStrLong((Date)targ_value),sb);
					index++;
				}

			}else if(db_value instanceof BigDecimal){
				if(toLong((Long)db_value) != toLong((Long)targ_value )){
					log(index,vf,ObjectUtils.toString(db_value),ObjectUtils.toString(targ_value),sb);
					index++;
				}
			}else if(db_value instanceof List){
				List<AbstractPageEntity> dbList =(List<AbstractPageEntity>)db_value;
				List<AbstractPageEntity> tagList=(List<AbstractPageEntity>)targ_value;
				if(CollectionUtils.isNotEmpty(dbList)&& CollectionUtils.isNotEmpty(tagList)){
					boolean hasRz = false;
					for(AbstractPageEntity d:dbList){
						Field did = getId(d);
						if(did == null){
							break;
						}
						for(AbstractPageEntity t:tagList){
							Field tid =getId(t);
							if(tid == null){
								break;
							}
							String equalName =ObjectUtils.toString(getValue(did.getName(),d));
							//相同，则认为是同一条记录
							if(equalName.equals(getValue(tid.getName(), t))){
								String czrz = getCzrz(t, d);
								if(StringUtils.isBlank(czrz)){
									continue;
								}
								if(!hasRz){
									sb.append(index+".修改"+ vf.name() + ":<br/>");
									index ++ ;
									hasRz = true;
								}
								AsmsField _vf = tid.getAnnotation(AsmsField.class);
								sb.append(_vf.name()+"<font color='red'>"+equalName + "</font>修改:" +czrz.replaceAll("<br/>", "") +"<br/>");
								break;
							}
						}
					}
				}
			}else if(type.equals("int") || type.equals("double") || type.endsWith("long")){
				if(db_value != targ_value ){
					log(index,vf,ObjectUtils.toString(db_value),ObjectUtils.toString(targ_value),sb);
					index++;
				}
			}

		}

		return sb.toString();
	}
	private static Field getId(AbstractPageEntity m){
		Field[] fields = getDeclaredFields(m.getClass());
		for(Field f:fields){
			AsmsField vf = f.getAnnotation(AsmsField.class);
			if(vf == null){
				continue;
			}
			if(vf.equalField()){
				return f;
			}
		}
		return null;
	}


	/**
	 * 日志内容
	 * @param i  序号
	 * @param vf 注解
	 * @param db 数据库数据
	 * @param tag 修改后数据
	 * @param sb 输出
	 */
	private static void log(int i,AsmsField vf, String db, String tag,StringBuffer sb) {
		String name = vf.name();
		String _db=StringUtils.isBlank(db) ? "无" : db,_tag=StringUtils.isBlank(tag) ? "无" : tag;
		String[] options = vf.options();
		if(options != null && options.length != 0){
			_db = getOptionsValue(options,db);
			_tag = getOptionsValue(options, tag);
		}
		sb.append(i+".");
		sb.append(name+"由<font color='white'>");
		sb.append(_db);
		sb.append("</font>");
		sb.append("改为<font color='red'>");
		sb.append(_tag);
		sb.append(";</font>\n<br/>");

	}
	/**
	 * 获取字段枚举值
	 */
	private static String getOptionsValue(String[] options, String str) {
		if(StringUtils.isBlank(str)){
			return "无";
		}
		try {
			String[] strs = {str};
			String split = "";
			for(String _split : SPLITS){
				if(StringUtils.contains(str,_split)){
					strs = StringUtils.split(str,_split);
					split = _split;
					break;
				}
			}
			String res = "";
			for(String _str : strs){
				for(int i=0;i<options.length;i++){
					if(options[i].equals(_str)){
						res += (options[i+1] + split);
					}
				}
			}
			return StringUtils.removeEnd(res,split);
		} catch (Exception e) {
			//System.out.println("注解错误");
		}
		return "无";
	}
	private static int toInt(Integer i){
		if(i == null){
			return 0;
		}else{
			return i.intValue();
		}
	}
	private static double toDouble(Double i){
		if(i == null){
			return 0.0;
		}else{
			return i.doubleValue();
		}
	}
	private static long toLong(Long i){
		if(i == null){
			return 0l;
		}else{
			return i.longValue();
		}
	}

	/**
	 * 获取所有属性
	 */
	private static Field[] getDeclaredFields(Class<?> classz){
		Class<?> clazz = classz;
		List<Field> l =new ArrayList<Field>();
		for(; clazz != null; clazz = clazz.getSuperclass()) {
			Field[] fieldes = clazz.getDeclaredFields();
			l.addAll(Arrays.asList(fieldes));
		}
		return l.toArray(new Field[]{});
	}
	/**
	 * 获取bean属性值
	 */
	private static Object getValue(String fieldNam,AbstractPageEntity bean){
		try {

			PropertyDescriptor pd = new PropertyDescriptor(fieldNam, bean.getClass());
			Method getMethod = pd.getReadMethod();//获得get方法
			return getMethod.invoke(bean);//执行get方法返回一个Object
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 为bean属性赋值
	 */
	private static void setValue(String fieldNam,AbstractPageEntity bean,Object value){
		try {
			PropertyDescriptor pd = new PropertyDescriptor(fieldNam, bean.getClass());
			Method setMethod = pd.getWriteMethod();
			setMethod.invoke(bean, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据名字获取属性
	 */
	private static Field getField(String property,Class<?> clazz){
		if(clazz == null){
			return null;
		}
		try {
			return clazz.getDeclaredField(property);
		} catch (Exception e) {
			return getField(property,clazz.getSuperclass());
		}
	}
}
