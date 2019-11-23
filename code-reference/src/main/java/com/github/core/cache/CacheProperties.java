package com.github.core.cache;
/**
 * cachename的属性
 * @author 章磊
 *
 */
public class CacheProperties {
	private String cacheName;
	private long memoryStoreSize;	//内存元素个数
	private long size;				//总数量
	private long diskStoreSize;		//磁盘元素个数
	private long calculateInMemorySize;	//实际内存大小
	private long calculateOffHeapSize;	//实际内存大小
	
	public long getCalculateOffHeapSize() {
		return calculateOffHeapSize;
	}
	public void setCalculateOffHeapSize(long calculateOffHeapSize) {
		this.calculateOffHeapSize = calculateOffHeapSize;
	}
	public long getCalculateInMemorySize() {
		return calculateInMemorySize;
	}
	public void setCalculateInMemorySize(long calculateInMemorySize) {
		this.calculateInMemorySize = calculateInMemorySize;
	}
	public long getDiskStoreSize() {
		return diskStoreSize;
	}
	public void setDiskStoreSize(long diskStoreSize) {
		this.diskStoreSize = diskStoreSize;
	}
	public String getCacheName() {
		return cacheName;
	}
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	public long getMemoryStoreSize() {
		return memoryStoreSize;
	}
	public void setMemoryStoreSize(long memoryStoreSize) {
		this.memoryStoreSize = memoryStoreSize;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public long getCalculateInMemorySizeM(){
		return this.calculateInMemorySize/1024/1024;
	}
}
