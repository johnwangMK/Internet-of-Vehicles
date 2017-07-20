package cn.guet.haojiayou.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetService {

/**
 * 判断网络是否连接	
 * @param context
 * @return
 */
	public static boolean isNetworkConnected(	Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}
	/**
	 *  网络状态
	 * @param e
	 * @return
	 */
	public static String ExceptionCode(Exception e) {
		
		if (e instanceof HttpException) {
			    return "网络异常";
		} else if (e instanceof SocketTimeoutException) {
			return  "响应超时";

		} else if (e instanceof ConnectTimeoutException) {
			return "请求超时";

		} else if (e instanceof UnknownHostException) { 
			return "无法连接服务器";

		} else if (e instanceof IOException) {
			return "网络异常";
		} 
		return "很好很好";
		
	}
	/**
	 * 获取服务器数据
	 * @param path
	 * @return
	 * @throws Exception  网络异常
	 */
    public static String getJSONcars(String path) throws Exception{
        URL url = new URL(path.toString());
        //打开程序和URL之间的通信连接,我们可以从网页中获取网页数据  
    	HttpURLConnection conn= (HttpURLConnection)url.openConnection();
    	conn.setConnectTimeout(5000);
    	conn.setRequestMethod("GET");
    	if(conn.getResponseCode() == 200){
    		//打开与此URL的连接，并返回一个用于读取该URL资源的InputStream
    		InputStream is =  conn.getInputStream();// 获取输入流  
    		
    		System.out.println(is);
    		return parseJSON(is);
    	}else if (conn.getResponseCode() == 404 ) {return "404" ;}
        else if (conn.getResponseCode() == 500 ) {return "500" ;}
    	return null;
    }
    
    public static String parseJSON(InputStream is) throws Exception{
    	byte[] data =null;
    	//把数据输入流转换成字节数组
    	data = StreamTool.readInputStream(is);
    	//把字节数组转换成json字符串
    	String carjson = new String(data);  		 	
		return carjson ;
    	
    }
    
    /**
     * 向服务器传输数据
     * @param path
     * @param jsonstr
     * @throws Exception
     */
	public static String sendData(String path, String jsonstr) throws Exception {
		String msg = null;
		URL url = new URL(path.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);

		// ****给服务器发送数据
		BufferedOutputStream out = new BufferedOutputStream(
				conn.getOutputStream());// 获取输出流
		out.write(jsonstr.getBytes());// 把数据写到报文
		out.flush();
		out.close();

		BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(bis,
				"utf-8"));// 发送请求
		// ****给服务器发送数据
		
		//*****接受服务器返回的数据
		if (conn.getResponseCode() == 200) {
			// 内存流
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int len = 0;
			String result = null;
			InputStream inputStream = conn.getInputStream();

			if (inputStream != null) {
				try {
					while ((len = inputStream.read(data)) != -1) {
						byteArrayOutputStream.write(data, 0, len);
					}
					result = new String(byteArrayOutputStream.toByteArray(),
							"utf-8");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			inputStream.close();
			msg = result;
		}
		//*****接受服务器返回的数据
		
		reader.close();
		conn.disconnect();
		return msg;
	}
    
    /**
     * 向服务器传输数据
     * @param path
     * @param jsonstr
     * @throws Exception
     */
    public static void sendData1(String path,String jsonstr) throws Exception {
    	
  	  URL url = new URL(path.toString()); 
    	HttpURLConnection conn= (HttpURLConnection)url.openConnection();
    	//conn.setConnectTimeout(5000);
    	conn.setRequestMethod("POST");
    	conn.setDoOutput(true);
      System.out.println("carJson222222222"+jsonstr);
//  	//if(conn.getResponseCode() == 200){
//  		//打开与此URL的连接，并返回一个用于向该URL资源传输数据的OutputStream
//  		OutputStream out =  conn.getOutputStream();// 获取输出流  
//  		out.write(("datalist"+jsonstr).getBytes());//把数据写到报文
//  	    out.flush();
//  	    out.close();
//  	    conn.getInputStream();//发送请求
//  	//}
    	if(conn.getResponseCode() == 200){
    		
    		System.out.println("conn.getResponseCode():"+conn.getResponseCode());
    		System.out.println("++++carJson"+jsonstr);
    		
  	    BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());// 获取输出流 
  	    // String jsonstr1 = URLEncoder.encode(jsonstr,"UTF-8"); 
  	    
  	    out.write(jsonstr.getBytes());//把数据写到报文
          out.flush();
          out.close();

          BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
          BufferedReader reader = new BufferedReader(new InputStreamReader(bis,"utf-8"));//发送请求
          StringBuffer result = new StringBuffer();
          while (reader.ready()) {
              result.append((char)reader.read());
          }
  		System.out.println(result.toString());
  		reader.close();		
    	}
	} 
    
    /**
     * 获取网络图片
     * @param path 
     * @return 
     */
	public static Drawable getNetImage(String path)
	{
		
		if(null == path)
		 return null;
		
		try
		{
			 URL url = new URL(path.toString()); 
			HttpURLConnection connection =(HttpURLConnection) url.openConnection();
			InputStream is = connection.getInputStream();
			return Drawable.createFromStream(is, "image");
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	 /** 
     * 通过get方式提交参数给服务器 
     */  
    public static String sendGetRequest(String urlPath,  
            Map<String, String> params, String encoding) throws Exception {  
  
        // 使用StringBuilder对象  
        StringBuilder sb = new StringBuilder(urlPath);  
        sb.append('?');  
  
        // 迭代Map  
        for (Map.Entry<String, String> entry : params.entrySet()) {  
            sb.append(entry.getKey()).append('=').append(  
                    URLEncoder.encode(entry.getValue(), encoding)).append('&');  
        }  
        //删掉最后一个&  
        sb.deleteCharAt(sb.length() - 1);  
        // 打开链接  
        URL url = new URL(sb.toString());  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setRequestMethod("GET");  
        conn.setRequestProperty("Content-Type", "text/xml");  
        conn.setRequestProperty("Charset", encoding);  
        conn.setConnectTimeout(5000);  
        // 如果请求响应码是200，则表示成功  
        if (conn.getResponseCode() == 200) {  
            // 获得服务器响应的数据  
            BufferedReader in = new BufferedReader(new InputStreamReader(conn  
                    .getInputStream(), encoding));  
            // 数据  
            String retData = null;  
            String responseData = "";  
            while ((retData = in.readLine()) != null) {  
                responseData += retData;  
            }  
            in.close(); 
            System.out.println("服务器响应的值是："+responseData );
            return responseData;  
        }  else if (conn.getResponseCode() == 404 ) {return "404" ;}
        else if (conn.getResponseCode() == 500 ) {return "500" ;}
       return "404";
    }  
}
