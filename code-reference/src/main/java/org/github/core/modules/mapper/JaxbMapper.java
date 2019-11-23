/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.github.core.modules.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.github.core.modules.utils.Exceptions;
import org.github.core.modules.utils.Reflections;
import org.github.core.modules.utils.StrUtil;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 使用Jaxb2.0实现XML<->Java Object的Mapper.
 * 
 * 在创建时需要设定所有需要序列化的Root对象的Class.
 * 特别支持Root对象是Collection的情形.
 * 
 * @author calvin
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JaxbMapper {

	private static ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();

	/**
	 * Java Object->Xml without encoding.
	 */
	public static String toXml(Object root) {
		Class clazz = Reflections.getUserClass(root);
		return toXml(root, clazz, "UTF-8",true);
	}
    public static String toXmlNoHead(Object root) {
        Class clazz = Reflections.getUserClass(root);
        return toXml(root, clazz, "UTF-8",false);
    }
	/**
	 * Java Object->Xml with encoding.
	 */
	public static String toXml(Object root, String encoding) {
		Class clazz = Reflections.getUserClass(root);
		return toXml(root, clazz, encoding,true);
	}

	/**
	 * Java Object->Xml with encoding.
	 */
	public static String toXml(Object root, Class clazz, String encoding,boolean head) {
		try {
			StringWriter writer = new StringWriter();
			createMarshaller(clazz, encoding,head).marshal(root, writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * Java Collection->Xml without encoding, 特别支持Root Element是Collection的情形.
	 */
	public static String toXml(Collection<?> root, String rootName, Class clazz) {
		return toXml(root, rootName, clazz, null);
	}

	/**
	 * Java Collection->Xml with encoding, 特别支持Root Element是Collection的情形.
	 */
	public static String toXml(Collection<?> root, String rootName, Class clazz, String encoding) {
		try {
			CollectionWrapper wrapper = new CollectionWrapper();
			wrapper.collection = root;

			JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(new QName(rootName),
					CollectionWrapper.class, wrapper);

			StringWriter writer = new StringWriter();
			createMarshaller(clazz, encoding,true).marshal(wrapperElement, writer);

			return writer.toString();
		} catch (JAXBException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * Xml->Java Object.
	 */
	public static <T> T fromXml(String xml, Class<T> clazz) {
		try {
			StringReader reader = new StringReader(xml);
			return (T) createUnmarshaller(clazz).unmarshal(reader);
		} catch (JAXBException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 创建Marshaller并设定encoding(可为null).
	 * 线程不安全，需要每次创建或pooling。
	 */
	public static Marshaller createMarshaller(Class clazz, String encoding,boolean head) {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);

			Marshaller marshaller = jaxbContext.createMarshaller();
            if(head) {
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            }else{
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            }

			if (StringUtils.isNotBlank(encoding)) {
				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			}

			return marshaller;
		} catch (JAXBException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 创建UnMarshaller.
	 * 线程不安全，需要每次创建或pooling。
	 */
	public static Unmarshaller createUnmarshaller(Class clazz) {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);
			return jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw Exceptions.unchecked(e);
		}
	}

	protected static JAXBContext getJaxbContext(Class clazz) {
		Validate.notNull(clazz, "'clazz' must not be null");
		JAXBContext jaxbContext = jaxbContexts.get(clazz);
		if (jaxbContext == null) {
			try {
				jaxbContext = JAXBContext.newInstance(clazz, CollectionWrapper.class);
				jaxbContexts.putIfAbsent(clazz, jaxbContext);
			} catch (JAXBException ex) {
				throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: "
						+ ex.getMessage(), ex);
			}
		}
		return jaxbContext;
	}

	/**
	 * 封装Root Element 是 Collection的情况.
	 */
	public static class CollectionWrapper {

		@XmlAnyElement
		protected Collection<?> collection;
	}
	public static String getXmlHead(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	}
	/**
	 * @category 把list集合转成xml
	 * @param list 要转换的list集合
	 * @param rootname list的根节点
	 * @param head 是否需要<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> 头
	 */
	@SuppressWarnings("unchecked")
	public static String toXml(List list, String rootname, boolean head){

		StringBuffer sb = null;
		if(head){
			sb = new StringBuffer(getXmlHead());
		}else{
			sb = new StringBuffer();
		}
		if(org.apache.commons.lang.StringUtils.isNotBlank(rootname)){
			sb.append("<"+rootname+">");
		}

		for(Object o : list){
			sb.append(JaxbMapper.toXmlNoHead(o));
		}
		if(org.apache.commons.lang.StringUtils.isNotBlank(rootname)){
			sb.append("</"+rootname+">");
		}
		return sb.toString();
	}

	/**
	 *
	 * @param list
	 *            将list里面的map转换成xml,
	 * @param segmentTag
	 *            行标记
	 * @return xml
	 */
	public static String toXml(List<Map<String, String>> list, String segmentTag) {
		StringBuffer xml = new StringBuffer();
		if (list == null || list.isEmpty()) {
			xml.append("<").append(segmentTag).append(">");
			xml.append("</").append(segmentTag).append(">");
			return xml.toString();
		}
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Map<String, String> m = (Map<String, String>) it.next();
			xml.append("<").append(segmentTag).append(">");
			xml.append(toXml(m, null));
			xml.append("</").append(segmentTag).append(">");
		}

		return xml.toString();
	}
	// 将Map里面的内容转换成xml
	public static String toXml(Map<String, String> m, String rootTag) {
		StringBuffer xml = new StringBuffer();
		if(null != m && !m.isEmpty()){
			Iterator it = m.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				xml.append("<").append(key).append(">").append(StrUtil.toXmlFormat(org.apache.commons.lang.StringUtils.trimToEmpty(m.get(key)))).append("</").append(key)
						.append(">");
			}
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(rootTag)) {
			return "<" + rootTag + ">" + xml.toString() + "</" + rootTag + ">";
		}
		return xml.toString();
	}

	public static void main(String[] args) {
		String resultXml = "<SQL><HYID>151013163549844365</HYID><CLKID>170301112351724647</CLKID><RY><RYID>170518095102408872</RYID><XM>黄鹏</XM><ZJLX>B</ZJLX><ZJHM>1234556673</ZJHM><SJHM>13656556581</SJHM></RY><RY><RYID>2017042411043589778</RYID><XM>柴伶俐</XM><ZJLX>B</ZJLX><ZJHM>420682199311034520</ZJHM><SJHM>13886069999</SJHM></RY></SQL>";
		resultXml="<ROOT>" +
				"<HYID>151013163549844365</HYID>" +
				"<CLKID>170301112351724647</CLKID>" +
				"</ROOT>";
		Map<String,String> m=fromXml(resultXml,HashMap.class);
		System.out.print(m);
	}
}
