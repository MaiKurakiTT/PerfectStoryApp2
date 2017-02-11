package cn.jinmiao.bbs.perfectstoryapp.utils;

/**
 * @author fairy
 * Get 或者Post 回调接口 实现此接口方法便可以处理服务器返回数据
 * */
public interface MyHttpCallbackListenerUtil {

	void onFinish(String response);
	void onError(Exception e);
}
