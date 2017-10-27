package com.my.blog.website.utils;

import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 *
 * 七牛上传通用方法
 * Created by troubleMan
 * on 2017/7/11.
 */
public class QiniuUploadUtil {

	private static final String ACCESS_KEY="NjG7excmdg2GjwASwO0xEzLo1cGYa0w-FAV1aeYZ";
	private static final String SECRETKEY="M7u1h4iBP_SuCQFtrmmy0AjLoKZA9paHFLHQ81cT";
	private static final String BUCKET = "troubleman";
	//默认的路劲地址
	private static final String DEFAULT_URL="http://odqld18xa.bkt.clouddn.com/";

	private static final Logger LOGGER = LoggerFactory.getLogger(QiniuUploadUtil.class);

	/**
	 * 上传图片到七牛云
	 * 返回图片路径
	 */
	public static String uploadToQiniu(MultipartFile file){
		String uploadResult ="";
		try {
			//构造一个带指定Zone对象的配置类
	        Configuration cfg = new Configuration(Zone.zone0());
	        //...其他参数参考类注释
	        UploadManager uploadManager = new UploadManager(cfg);
	        //生成文件名
	        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
	        StringBuffer fileBufferName = new StringBuffer(UUID.randomUUID().toString().replaceAll("-", ""));
	        fileBufferName.append(suffix);
	        //key为七牛返回的key
	        String key = "upload/"+fileBufferName;
	        Auth auth = Auth.create(ACCESS_KEY, SECRETKEY);
	        String upToken = auth.uploadToken(BUCKET);
	        byte[] buffer;
			buffer = file.getBytes();
			Response qnResponse = uploadManager.put(buffer, key, upToken);
		    //解析上传成功的结果
		    DefaultPutRet putRet = new Gson().fromJson(qnResponse.bodyString(), DefaultPutRet.class);
		    uploadResult = DEFAULT_URL+putRet.key;
		} catch (Exception e) {
			LOGGER.error("upload picture to qiniu error:", e);
		}
		return uploadResult;
	}
}
