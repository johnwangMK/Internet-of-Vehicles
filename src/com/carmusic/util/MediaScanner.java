package com.carmusic.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class MediaScanner {
	private MediaScannerConnection mConnection = null;
	private MusicSannerClient mClient = null;

	private String filePath = null;
	private String fileType = null;

	public MediaScanner(Context context) {
		mClient = new MusicSannerClient();
		mConnection = new MediaScannerConnection(context, mClient);
	}

	private class MusicSannerClient implements MediaScannerConnectionClient {
		@Override
		public void onMediaScannerConnected() {
			mConnection.scanFile(filePath, fileType);
		}

		@Override
		public void onScanCompleted(String path, Uri uri) {
			mConnection.disconnect();
		}
	}

	/**
	 * 扫描某文件到媒体库中
	 * */
	public void scanFile(String filePath, String fileType) {
		this.filePath = filePath;
		this.fileType = fileType;
		mConnection.connect();
	}
}
