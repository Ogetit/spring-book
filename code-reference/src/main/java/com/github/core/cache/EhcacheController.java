package com.github.core.cache;

import net.sf.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.github.core.modules.cache.EhcacheManage;
import org.github.core.modules.web.BaseControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 缓存管理
 * @author 章磊
 *
 */
@Controller
public class EhcacheController extends BaseControl {
	@Autowired
	private EhcacheManage ehcacheManage;
	@RequestMapping(value="list")
	public String list(@RequestParam Map<String, String> param,ModelMap modelMap){
		String[] cacheNames = ehcacheManage.getCacheNames();
		List<CacheProperties> list = new ArrayList<CacheProperties>();
		for(String name : cacheNames){
			Cache cache = ehcacheManage.getCache(name);
			CacheProperties cachename = new CacheProperties();
			//计算内存大小有性能问题
//			cachename.setCalculateOffHeapSize(cache.calculateOffHeapSize());
//			cachename.setCalculateInMemorySize(cache.calculateInMemorySize());
			cachename.setCacheName(name);
			cachename.setMemoryStoreSize(cache.getMemoryStoreSize());
			cachename.setDiskStoreSize(cache.getDiskStoreSize());
			cachename.setSize(cache.getSize());
			list.add(cachename);
		}
		modelMap.addAttribute("list",list);
		return viewpath("list");
	}
}
