package com.carmusic.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.carmusic.dao.AlbumDao;
import com.carmusic.dao.ArtistDao;
import com.carmusic.dao.DownLoadInfoDao;
import com.carmusic.dao.SongDao;
import com.carmusic.dao.ThreadInfoDao;
import com.carmusic.data.SystemSetting;
import com.carmusic.entity.Album;
import com.carmusic.entity.Artist;
import com.carmusic.entity.DownLoadInfo;
import com.carmusic.entity.Song;
import com.carmusic.entity.ThreadInfo;
import com.carmusic.util.Common;
import com.carmusic.util.ComparatorDownLoadInfo;
import com.carmusic.util.MediaScanner;

public class DownLoadService extends Service {

	private final IBinder mBinder = new DownLoadBinder();
	private final int DOWNLOAD_THREAD = 3;// 每个下载任务的线程数量
	private final int DOWNLOAD_TIMEOUT = 5000;// 下载超时毫秒数

	private Hashtable<String, DownLoadInfo> downLoadMap = null;
	private DownLoadInfoDao downLoadInfoDao;
	private ThreadInfoDao threadInfoDao;
	private SongDao songDao;
	private AlbumDao albumDao;
	private ArtistDao artistDao;
	private int taskCount=0;// 当前执行的下载任务数量
	private MediaScanner mediaScanner;
	private ComparatorDownLoadInfo comparatorDownLoadInfo;
	private boolean isStop=false;
	private String url;
	private boolean isError=false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		downLoadInfoDao = new DownLoadInfoDao(this);
		threadInfoDao = new ThreadInfoDao(this);
		songDao = new SongDao(this);
		artistDao = new ArtistDao(this);
		albumDao = new AlbumDao(this);
		mediaScanner = new MediaScanner(this);
		comparatorDownLoadInfo = new ComparatorDownLoadInfo();
		// 实例化dao
		downLoadMap = new Hashtable<String, DownLoadInfo>();
		List<DownLoadInfo> list = downLoadInfoDao.searchAll();
		isStop=false;
		for (DownLoadInfo downLoadInfo : list) {
			// 查询下载任务的子线程集合
			downLoadInfo.setThreadInfos(threadInfoDao
					.searchByDownLoadInfoId(downLoadInfo.getId()));
			downLoadMap.put(downLoadInfo.getUrl(), downLoadInfo);
		}
	}

	public class DownLoadBinder extends Binder {
		public DownLoadService getService() {
			return DownLoadService.this;
		}
	}

	
	//onStartCommand
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(intent.getAction().equals(DownLoadManager.SERVICE_ACTION)){
			int flag=intent.getIntExtra("flag", -1);
			//停止下载
			if(flag==DownLoadManager.SERVICE_DOWNLOAD_STOP){
				stop();
			}
		}
	}

	/**
	 * 获取下载数据
	 * */
	public List<DownLoadInfo> getDownLoadData() {
		List<DownLoadInfo> list = new ArrayList<DownLoadInfo>();
		for (String key : downLoadMap.keySet()) {
			list.add(downLoadMap.get(key));
		}
		Collections.sort(list, comparatorDownLoadInfo);
		return list;
	}

	/**
	 * 下载
	 * */
	private void downLoad() {
		if(taskCount==0){
			String url = getNextDownLoadTask();
			if (url != null) {
				DownLoadInfo downLoadInfo = downLoadMap.get(url);
				downLoadInfo.setState(DownLoadManager.STATE_CONNECTION);
				taskCount++;
				// 更新前台
				sendBroadcast(new Intent(
						DownLoadManager.BROADCASTRECEVIER_ACTON).putExtra(
						"flag", DownLoadManager.FLAG_CHANGED));
				startDownLoad(downLoadInfo);
			}
		}
	}

	/**
	 * 获取下一个任务的url
	 * */
	private synchronized String getNextDownLoadTask() {
		if(downLoadMap==null){
			return null;
		}
		DownLoadInfo downLoadInfo = null;
		for (String key : downLoadMap.keySet()) {
			DownLoadInfo t_downLoadInfo = downLoadMap.get(key);
			// 将等待的任务启动，开始下载
			if (t_downLoadInfo.getState() == DownLoadManager.STATE_WAIT) {
				if (downLoadInfo == null) {
					downLoadInfo = t_downLoadInfo;
				} else {
					if (downLoadInfo.getId() > t_downLoadInfo.getId()) {
						downLoadInfo = t_downLoadInfo;
					}
				}
			}
		}
		if (downLoadInfo != null) {
			return downLoadInfo.getUrl();
		}
		return null;
	}

	/**
	 * 开始下载
	 * */
	private void startDownLoad(final DownLoadInfo downLoadInfo) {
		for (int i = 0; i < downLoadInfo.getThreadInfos().size(); i++) {
			final ThreadInfo threadInfo = downLoadInfo.getThreadInfos().get(i);
			final int j = i;
			isError=false;
			url=downLoadInfo.getUrl();
			new Thread(new Runnable() {
				@Override
				public void run() {
					HttpURLConnection connection = null;
					RandomAccessFile randomAccessFile = null;
					InputStream in = null;
					int completeSize = threadInfo.getCompleteSize();
					DownLoadInfo t_DownLoadInfo = downLoadMap.get(downLoadInfo
							.getUrl());
					if (t_DownLoadInfo == null) {
						return;
					}
					t_DownLoadInfo.setThreadCount(t_DownLoadInfo
							.getThreadCount() + 1);

					try {
						URL url = new URL(downLoadInfo.getUrl());
						connection = (HttpURLConnection) url.openConnection();
						connection.setConnectTimeout(DOWNLOAD_TIMEOUT);
						connection.setReadTimeout(DOWNLOAD_TIMEOUT);
						connection.setRequestMethod("GET");
						connection.setUseCaches(false);
						connection.setRequestProperty("Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
						connection.setRequestProperty("Accept-Language","zh-CN");
						connection.setRequestProperty("Referer",downLoadInfo.getUrl());
						connection.setRequestProperty("Charset", "UTF-8");
						connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
						connection.setRequestProperty("Connection","Keep-Alive");

						// 设置获取数据范围，格式为Range：bytes x-y;
						connection.setRequestProperty(
								"Range",
								"bytes="
										+ (threadInfo.getStartPosition() + completeSize)
										+ "-" + threadInfo.getEndPosition());
						randomAccessFile = new RandomAccessFile(downLoadInfo
								.getFilePath(), "rwd");
						randomAccessFile.seek(threadInfo.getStartPosition()
								+ completeSize);
						connection.connect();
						in = connection.getInputStream();
						byte[] buffer = new byte[1024*8];
						int length = -1;
						int count=0;
						
						while ((length = in.read(buffer)) != -1) {
							t_DownLoadInfo = downLoadMap.get(downLoadInfo
									.getUrl());
							if((t_DownLoadInfo.getState()==DownLoadManager.STATE_DELETE)
									||(t_DownLoadInfo.getState()==DownLoadManager.STATE_PAUSEING)){
								break;
							}
							// 写入本地文件
							randomAccessFile.write(buffer, 0, length);
							completeSize += length;
							// 更新下载任务总进度
							if(count==0){
								t_DownLoadInfo.setState(
									DownLoadManager.STATE_DOWNLOADING);
								count=1;
							}
							t_DownLoadInfo.getThreadInfos().get(j)
							.setCompleteSize(completeSize);
							t_DownLoadInfo.setCompleteSize(t_DownLoadInfo
									.getCompleteSize() + length);
							// 任务下载完成
							if (t_DownLoadInfo.getCompleteSize() == t_DownLoadInfo
									.getFileSize()) {
								//删除下载任务的子线程数据
								threadInfoDao
										.deleteByDownLoadInfoId(t_DownLoadInfo
												.getId());
								//删除下载任务
								downLoadInfoDao.delete(t_DownLoadInfo.getId());
								
								downLoadMap.remove(t_DownLoadInfo.getUrl());

								// 修改SD卡文件名
								t_DownLoadInfo.setFilePath(Common
										.renameFileName(t_DownLoadInfo
												.getFilePath()));
								// 保存到歌曲列表中
								addSong(t_DownLoadInfo);
								// 扫描歌曲到媒体库中
								mediaScanner.scanFile(
										t_DownLoadInfo.getFilePath(),
										t_DownLoadInfo.getMimeType());

								taskCount--;
								// 通知前台下载任务完成
								sendBroadcast(new Intent(
										DownLoadManager.BROADCASTRECEVIER_ACTON)
										.putExtra("flag",
												DownLoadManager.FLAG_COMPLETED)
										.putExtra(
												"displayname",
												Common.clearSuffix(downLoadInfo
														.getDisplayName())));
								start(getNextDownLoadTask());
								//更新播放列表
								startService(new Intent(DownLoadService.this,MediaPlayerService.class).putExtra("flag", MediaPlayerManager.SERVICE_RESET_PLAYLIST));
								break;
							}
							// 通知前台
							sendBroadcast(new Intent(
									DownLoadManager.BROADCASTRECEVIER_ACTON)
									.putExtra("flag",
											DownLoadManager.FLAG_CHANGED));
						}
						t_DownLoadInfo.setThreadCount(t_DownLoadInfo
								.getThreadCount() - 1);
						// 通知前台
						sendBroadcast(new Intent(
								DownLoadManager.BROADCASTRECEVIER_ACTON).putExtra(
								"flag", DownLoadManager.FLAG_CHANGED));
						
					} catch (SocketTimeoutException e) {// 超时
						doError(downLoadInfo.getUrl(),DownLoadManager.STATE_PAUSE,DownLoadManager.FLAG_TIMEOUT);
					} catch (FileNotFoundException e) {// 服务器找不到文件
						doError(downLoadInfo.getUrl(),DownLoadManager.STATE_FAILED,DownLoadManager.FLAG_FAILED);
					} catch (IOException e) {//未知错误
						doError(downLoadInfo.getUrl(),DownLoadManager.STATE_ERROR,DownLoadManager.FLAG_ERROR);
					} finally {
						try {
							if (in != null)
								in.close();
							if (randomAccessFile != null)
								randomAccessFile.close();
							if (connection != null)
								connection.disconnect();
						} catch (IOException e) {//未知错误
							doError(downLoadInfo.getUrl(),DownLoadManager.STATE_ERROR,DownLoadManager.FLAG_ERROR);
						}
					}
				}
			}).start();
		}
	}

	/**
	 * 发生错误时
	 * */
	private void doError(String url,int state,int flag) {
		isError=true;
		DownLoadInfo dInfo = downLoadMap.get(url);
		dInfo.setThreadCount(dInfo.getThreadCount() - 1);
		if (dInfo.getThreadCount() == 0) {//最后一个线程时
			taskCount--;
			if(dInfo.getState()==DownLoadManager.STATE_DELETE){
				downLoadMap.remove(url);
				threadInfoDao.deleteByDownLoadInfoId(dInfo.getId());
				downLoadInfoDao.delete(dInfo.getId());
				new File(dInfo.getFilePath()).delete();
				sendBroadcast(new Intent(DownLoadManager.BROADCASTRECEVIER_ACTON)
				.putExtra("flag", DownLoadManager.FLAG_COMMON));
				start(getNextDownLoadTask());
			}else if(dInfo.getState()==DownLoadManager.STATE_PAUSEING){
				//更新数据库
				threadInfoDao.update(dInfo.getThreadInfos());
				downLoadInfoDao.update(dInfo.getId(),
				downLoadMap.get(dInfo.getUrl())
						.getCompleteSize());
				dInfo.setState(DownLoadManager.STATE_PAUSE);
				sendBroadcast(new Intent(DownLoadManager.BROADCASTRECEVIER_ACTON)
				.putExtra("flag", DownLoadManager.FLAG_COMMON));
				start(getNextDownLoadTask());
			}else{
				// 保存到数据库
				threadInfoDao.update(dInfo.getThreadInfos());
				downLoadInfoDao.update(dInfo.getId(), dInfo.getCompleteSize());
				dInfo.setState(state);
				sendBroadcast(new Intent(DownLoadManager.BROADCASTRECEVIER_ACTON)
				.putExtra("flag", flag)
				.putExtra("displayname",
						Common.clearSuffix(dInfo.getDisplayName())));
				start(getNextDownLoadTask());
			}
			if(isStop){
				//停止服务
				stopSelf();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 开始下载任务
	 * */
	public void start(String url) {
		if(url==null){
			return;
		}
		DownLoadInfo downLoadInfo = downLoadMap.get(url);
		if (taskCount==0) {
			taskCount++;
			downLoadInfo.setState(DownLoadManager.STATE_CONNECTION);
			startDownLoad(downLoadInfo);
		} else {
			downLoadInfo.setState(DownLoadManager.STATE_WAIT);
		}
		sendBroadcast(new Intent(DownLoadManager.BROADCASTRECEVIER_ACTON)
				.putExtra("flag", DownLoadManager.FLAG_CHANGED));
	}

	/**
	 * 添加下载任务
	 * */
	public void add(Song song) {
		String localfile = Common.getSdCardPath()
				+ SystemSetting.DOWNLOAD_MUSIC_DIRECTORY;
		Common.isExistDirectory(localfile);
		// 变成临时文件，防止扫描到媒体库中
		localfile += song.getDisplayName() + ".temp";
		// 在SD卡上创建下载文件
		File file = new File(localfile);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			// 本地访问文件
			RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			accessFile.setLength(song.getSize());
			accessFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 包装成下载任务类
		DownLoadInfo downLoadInfo = new DownLoadInfo();
		downLoadInfo.setAlbum(song.getAlbum().getName());
		downLoadInfo.setArtist(song.getArtist().getName());
		downLoadInfo.setCompleteSize(0);
		downLoadInfo.setDurationTime(song.getDurationTime());
		downLoadInfo.setDisplayName(song.getDisplayName());
		downLoadInfo.setFileSize(song.getSize());
		downLoadInfo.setMimeType(song.getMimeType());
		downLoadInfo.setName(song.getName());
		downLoadInfo.setUrl(song.getNetUrl());
		downLoadInfo.setFilePath(localfile);
		downLoadInfo.setState(DownLoadManager.STATE_WAIT);// 设置等待下载
		// 添加到下载任务表
		int downLoadInfoId = downLoadInfoDao.add(downLoadInfo);
		downLoadInfo.setId(downLoadInfoId);
		int range = song.getSize() / DOWNLOAD_THREAD;
		ThreadInfo threadInfo = null;
		List<ThreadInfo> threadInfos = new ArrayList<ThreadInfo>();
		// 计算子线程
		for (int i = 0; i < DOWNLOAD_THREAD; i++) {
			threadInfo = new ThreadInfo();
			threadInfo.setCompleteSize(0);
			threadInfo.setDownLoadInfoId(downLoadInfoId);
			threadInfo.setStartPosition(i * range);
			// 防止四舍五入误差
			if (i == DOWNLOAD_THREAD - 1) {
				threadInfo.setEndPosition(song.getSize());
			} else {
				threadInfo.setEndPosition(((i + 1) * range) - 1);
			}
			int threadInfoId = threadInfoDao.add(threadInfo);
			threadInfo.setId(threadInfoId);
			threadInfos.add(threadInfo);
		}
		downLoadInfo.setThreadInfos(threadInfos);
		downLoadMap.put(song.getNetUrl(), downLoadInfo);

		sendBroadcast(new Intent(DownLoadManager.BROADCASTRECEVIER_ACTON)
				.putExtra("flag", DownLoadManager.FLAG_WAIT).putExtra(
						"displayname",
						Common.clearSuffix(downLoadInfo.getDisplayName())));
		// 下载
		downLoad();
	}

	/**
	 * 删除下载任务
	 * */
	public void delete(final String url) {
		DownLoadInfo downLoadInfo = downLoadMap.get(url);
		if (downLoadInfo == null) {
			return;
		}
		int state=downLoadInfo.getState();
		//停止下载时，把等待任务的变成暂停任务
		if(isStop){
			for (String key : downLoadMap.keySet()) {
				DownLoadInfo t_downLoadInfo = downLoadMap.get(key);
				if(t_downLoadInfo.getState()==DownLoadManager.STATE_WAIT){
					t_downLoadInfo.setState(DownLoadManager.STATE_PAUSE);
				}
			}
		}
		if (state== DownLoadManager.STATE_DOWNLOADING||state== DownLoadManager.STATE_CONNECTION) {
			downLoadInfo.setState(DownLoadManager.STATE_DELETE);
			// 监听子线程下载是否全部中断
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						DownLoadInfo dInfo = downLoadMap.get(url);
						if (dInfo == null) {
							break;
						}
						if(isError){
							break;
						}
						if (dInfo.getThreadCount() == 0) {
							threadInfoDao.deleteByDownLoadInfoId(dInfo.getId());
							downLoadInfoDao.delete(dInfo.getId());
							downLoadMap.remove(dInfo.getUrl());
							new File(dInfo.getFilePath()).delete();
							taskCount--;
							if(!isStop){
								downLoad();
							}
							break;
						}
					}
					if(!isError){
						// 更新前台
						sendBroadcast(new Intent(
								DownLoadManager.BROADCASTRECEVIER_ACTON).putExtra(
								"flag", DownLoadManager.FLAG_CHANGED));
						if(isStop){
							//停止服务
							stopSelf();
						}
					}
				}
			}).start();
		} else {
			if(state==DownLoadManager.STATE_CONNECTION){
				downLoadInfo.setState(DownLoadManager.STATE_DELETE);
			}else{
				// 删除
				downLoadMap.remove(url);
				threadInfoDao.deleteByDownLoadInfoId(downLoadInfo.getId());
				downLoadInfoDao.delete(downLoadInfo.getId());
				new File(downLoadInfo.getFilePath()).delete();
			}
		}
		// 更新前台
		sendBroadcast(new Intent(DownLoadManager.BROADCASTRECEVIER_ACTON)
				.putExtra("flag", DownLoadManager.FLAG_CHANGED));
	}

	/**
	 * 暂停下载任务
	 * */
	public void pause(final String url) {
		if (downLoadMap.get(url) == null) {
			return;
		}
		int state=downLoadMap.get(url).getState();
		if (state == DownLoadManager.STATE_PAUSEING) {
			return;
		}
		if(isStop){
			//停止下载时，把等待任务的变成暂停任务
			for (String key : downLoadMap.keySet()) {
				DownLoadInfo t_downLoadInfo = downLoadMap.get(key);
				if(t_downLoadInfo.getState()==DownLoadManager.STATE_WAIT){
					t_downLoadInfo.setState(DownLoadManager.STATE_PAUSE);
				}
			}
		}
		if(state==DownLoadManager.STATE_WAIT){
			downLoadMap.get(url).setState(DownLoadManager.STATE_PAUSE);
		}else{
			downLoadMap.get(url).setState(DownLoadManager.STATE_PAUSEING);
		}
		if(state==DownLoadManager.STATE_DOWNLOADING||state== DownLoadManager.STATE_CONNECTION){
			// 监听子线程下载是否全部中断
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						DownLoadInfo dInfo = downLoadMap.get(url);
						if (dInfo == null) {
							break;
						}
						if(isError){
							break;
						}
						if (dInfo.getThreadCount() == 0) {
							//更新数据库
							threadInfoDao.update(dInfo.getThreadInfos());
							downLoadInfoDao.update(dInfo.getId(),
							downLoadMap.get(dInfo.getUrl())
									.getCompleteSize());
							
							dInfo.setState(DownLoadManager.STATE_PAUSE);
							taskCount--;
							if(!isStop){
								downLoad();
							}
							break;
						}
					}
					if(!isError){
						// 更新前台
						sendBroadcast(new Intent(
								DownLoadManager.BROADCASTRECEVIER_ACTON).putExtra(
								"flag", DownLoadManager.FLAG_CHANGED));
						if(isStop){
							//停止服务
							stopSelf();
						}
					}
				}
			}).start();
		} 
		sendBroadcast(new Intent(DownLoadManager.BROADCASTRECEVIER_ACTON)
		.putExtra("flag", DownLoadManager.FLAG_CHANGED));
	}

	/**
	 * 停止下载
	 * */
	public void stop(){
		if(TextUtils.isEmpty(url)){
			stopSelf();
			return;
		}
		if(taskCount==0){
			stopSelf();
			return;
		}
		isStop=true;
		pause(url);
	}
	
	public IBinder getBinder() {
		return mBinder;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	/**
	 * 将下载完成的任务添加到歌曲表中
	 * */
	private boolean addSong(DownLoadInfo downLoadInfo) {
		Song song = new Song();
		// 处理专辑
		Album album = new Album(-1, downLoadInfo.getAlbum(), "");
		int albumId = -1;
		albumId = albumDao.isExist(album.getName());
		if (albumId == -1) {
			albumId = (int) albumDao.add(album);
		}
		album.setId(albumId);
		song.setAlbum(album);

		// 处理歌手
		Artist artist = new Artist(-1, downLoadInfo.getArtist(), "");
		int artistId = -1;
		artistId = artistDao.isExist(artist.getName());
		if (artistId == -1) {
			artistId = (int) artistDao.add(artist);
		}
		artist.setId(artistId);
		song.setArtist(artist);

		song.setDisplayName(downLoadInfo.getDisplayName());
		song.setDownFinish(true);
		song.setDurationTime(downLoadInfo.getDurationTime());
		song.setFilePath(downLoadInfo.getFilePath());
		song.setLike(false);
		song.setLyricPath(null);
		song.setMimeType(downLoadInfo.getMimeType());
		song.setName(downLoadInfo.getName());
		song.setNet(false);
		song.setNetUrl(downLoadInfo.getUrl());
		song.setPlayerList("$1$");
		song.setSize(downLoadInfo.getFileSize());

		return songDao.add(song) > 0;
	}
}
