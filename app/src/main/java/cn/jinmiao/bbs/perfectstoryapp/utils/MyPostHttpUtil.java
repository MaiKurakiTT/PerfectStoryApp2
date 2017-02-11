package cn.jinmiao.bbs.perfectstoryapp.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author fairy
 *
 * Post 通用封装工具类
 * */
public class MyPostHttpUtil {
	
	public static void myPostHttpRequestUtils(final String address,final String param,final MyHttpCallbackListenerUtil listenerUtil){
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection httpURLConnection=null;
				PrintWriter out = null;
				try {

					
					URL url=new URL(address);
					// 打开和URL之间的连接
					httpURLConnection=(HttpURLConnection)url.openConnection();
					
					 // 设置通用的请求属性
					httpURLConnection.setRequestProperty("accept", "*/*");
					httpURLConnection.setRequestProperty("connection", "Keep-Alive");
					httpURLConnection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setRequestProperty("Charset", "UTF-8");
					
					// 发送POST请求必须设置如下两行
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(true);

			        // 获取URLConnection对象对应的输出流
		            out = new PrintWriter(httpURLConnection.getOutputStream());
		            // 发送请求参数
		            out.print(param);
		            // flush输出流的缓冲
		            out.flush();
					
		            InputStream inputStream=httpURLConnection.getInputStream();
		            // 定义BufferedReader输入流来读取URL的响应
					BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder response=new StringBuilder();
					String line;
					while ((line=bufferedReader.readLine())!=null) {
						response.append(line);
					}
					if(listenerUtil!=null){
						listenerUtil.onFinish(response.toString());
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					if(listenerUtil!=null){
						listenerUtil.onError(e);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(listenerUtil!=null){
						listenerUtil.onError(e);
					}
				}finally{
					if(httpURLConnection!=null){
						httpURLConnection.disconnect();
					}
				}
			}
		}).start();
	}
}
