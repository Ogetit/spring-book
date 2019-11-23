package org.github.core.modules.mybatis.page;
/**
 * 启动分页
 * 只要在任何查询前面执行了start那么就会启动分页拦截器
 * @author 章磊
 *
 */
public class PageHelper {
	private final static ThreadLocal<PageNew> LOCAL_PAGE = new ThreadLocal<PageNew>();
	public static void start(PageNew page){
		LOCAL_PAGE.set(page);
	}
	public static PageNew get(){
		return LOCAL_PAGE.get();
	}
	public static void end(){
		LOCAL_PAGE.remove();
	}

}
