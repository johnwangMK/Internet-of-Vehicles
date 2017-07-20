package com.wang.juhe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Juhe {
	public static final String DEF_CHARSET = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 3000;
	public static final int DEF_READ_TIMEOUT = 3000;
	
	//�����KEY
	public static final String APPKRY ="36837ff45775320ca9a59d632280544e";
	
	/**
	 * �����ܱ߼���վ,����StationInfo����
	 * @param lon
	 * @param lat
	 * @return
	 * @throws Exception
	 */
	public static StationInfo getRequest(double lon,double lat) throws Exception{
		
		String result = null;
		StationInfo info;
		String url = "http://apis.juhe.cn/oil/local";
		Map<String, Object> params =new HashMap<String, Object>();
			params.put("lon", lon);
			params.put("lat", lat);
			params.put("r", 8000); 
			params.put("key", APPKRY);
			result = net(url, params, "GET");
			result = result.replaceAll("93#", "P93");
			result = result.replaceAll("97#", "P97");
			result = result.replaceAll("0#车柴", "P0");
			result = result.replaceAll("90#", "P90");
			Gson gson = new Gson();
			info = gson.fromJson(result, StationInfo.class);
/*			Data[] data = info.getResult().getData();
			for(int i=0;i<data.length;i++){
				System.out.println(data[i].getGastprice().getP95());
			}*/
		return info;
		
	}
	/**
	 * �����ܱ߼���վ������json�ַ�
	 * @param lon
	 * @param lat
	 * @return
	 * @throws Exception
	 */
	public static String getStringRequest(double lon,double lat) throws Exception{
		
		String result = null;
		String url = "http://apis.juhe.cn/oil/local";
		Map<String, Object> params =new HashMap<String, Object>();
			params.put("lon", lon);
			params.put("lat", lat);
			params.put("r", 8000); 
			params.put("key", APPKRY);
			result = net(url, params, "GET");
		return result;
		
	}
	
	public static String net(String strUrl, Map<String, Object> params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHARSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }
	
	//��map��תΪ���������
    @SuppressWarnings("rawtypes")
	public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
