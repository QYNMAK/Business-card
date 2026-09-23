package com.pfh.crm.dashboard.common.util;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/10
 * @description ：
 * @version: 1.0
 */

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.FileInputStream;
import java.util.UUID;

/**
 * 七牛云工具类
 */
public class QiniuUtils {
	public  static String qiniu_img_url_pre = "http://psuyuqoog.bkt.clouddn.com/";
	public  static String accessKey = "T4DM8s-9C-r-uI33gpn6qt904fN2Khy7oQcMbyi7";
	public  static String secretKey = "NTtHrrIxy0txAAGwOd8__HDY9HcPbyQElOUM3hmn";
	public  static String bucket = "itcast_health";
	/**
	 * 上传文件
	 * @param bytes 文件内容，字节数组
	 * @param uploadFileName 保存到服务端的文件名
	 */
	public static void upload2Qiniu(byte[] bytes, String uploadFileName){
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());
		//...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);

		//默认不指定key的情况下，以文件内容的hash值作为文件名
		String key = uploadFileName;
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket);
		try {
			Response response = uploadManager.put(bytes, key, upToken);
			//解析上传成功的结果
			System.out.println(response.bodyString());
			// 访问路径
			System.out.println("http://psuyuqoog.bkt.clouddn.com/"+uploadFileName);
		} catch (QiniuException ex) {
			Response r = ex.response;
			System.err.println(r.toString());
			try {
				System.err.println(r.bodyString());
			} catch (QiniuException ex2) {
				ex.printStackTrace();
			}
		}
	}
	// 测试上传
	public static void main(String args[]) throws Exception{
		// 测试上传
		String localFilePath = "D:\\idea_project\\tmp\\test.jpg";
		FileInputStream fileInputStream = new FileInputStream(localFilePath);
		byte[] data = new byte[1024*1024];
		fileInputStream.read(data);
		String saveFileName = UUID.randomUUID().toString().replace("-","");
		QiniuUtils.upload2Qiniu(data,saveFileName);
	}
}
