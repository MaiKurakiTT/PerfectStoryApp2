package cn.jinmiao.bbs.perfectstoryapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author fairy
 *
 * Get 通用封装工具类
 * */
public class MyGetHttpUtil {
	
	public static void mySendHttpRequestUtils(final String address,final MyHttpCallbackListenerUtil listenerUtil){
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection httpURLConnection=null;
				try {
		
					URL url=new URL(address);
					
					httpURLConnection=(HttpURLConnection)url.openConnection();
				
					httpURLConnection.setRequestMethod("GET");
					
					httpURLConnection.setConnectTimeout(8000);
					
					httpURLConnection.setReadTimeout(8000);
					httpURLConnection.setRequestProperty("Charset", "UTF-8");

					httpURLConnection.setDoInput(true);

					httpURLConnection.setDoOutput(true);
					

					InputStream inputStream=httpURLConnection.getInputStream();
					

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
