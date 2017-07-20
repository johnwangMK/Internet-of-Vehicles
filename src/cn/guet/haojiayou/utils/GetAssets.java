package cn.guet.haojiayou.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class GetAssets {
	
	
	/**
	 * 读取图片
	 * @param fileName
	 * @return
	 */
	public static Drawable getImageFromAssetsFile(String fileName,Context context) 
	{ 
		Drawable image =null;
	
	AssetManager am = context.getResources().getAssets(); 
	try 
	{ 
	InputStream is = am.open(fileName); 
	Bitmap bitmap = BitmapFactory.decodeStream(is); 
	image = new BitmapDrawable(bitmap);
	is.close(); 
	} 
	catch (IOException e) 
	{ 
	e.printStackTrace(); 
	} 

	return image; 

	}

}
