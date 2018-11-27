package com.jinan.ladongjiguan.anjiantong.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.jinan.ladongjiguan.anjiantong.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageLoaderUtils {
	/**
	 * 图片批量加载配置
	 */
	public static ImageLoaderConfiguration initConfiguration(Context context) {
		return new ImageLoaderConfiguration.Builder(context)
//				.memoryCacheExtraOptions(480, 800) // default = device screen dimensions
//				.diskCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)//压缩图片用的？不造啊
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.memoryCacheSizePercentage(13)
				.diskCacheSize(50 * 1024 * 1024)
				.diskCacheFileCount(100)
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.imageDecoder(new BaseImageDecoder(true))
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs()
				.build();
	}

	/**
	 * 单张图片配置
	 */
	public static DisplayImageOptions initOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.setting_page_head)//正在下载时显示的图片
				.showImageForEmptyUri(R.drawable.setting_page_head)//下载地址为空时图片
				.showImageOnFail(R.drawable.setting_page_head)//下载失败时图片
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
//				.displayer(new RoundedBitmapDisplayer(20))
				.build();
	}
}