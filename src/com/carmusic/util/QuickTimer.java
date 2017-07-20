package com.carmusic.util;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class QuickTimer {

	private Timer mTimer;
	private TimerTask mTimerTask;
	private OnTimeListener mListener;
	private Handler mHandler;
	private boolean mFlag = false;

	public QuickTimer() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (mListener != null) {
					mListener.onTimer();
					mFlag = false;
				}
			}
		};
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		};
	}

	/**
	 * 开始任务
	 * */
	public void start(OnTimeListener listener, long interval) {
		this.mListener = listener;
		mTimer.schedule(mTimerTask, interval);
		mFlag = true;
	}

	/**
	 * 停止任务
	 * */
	public void stop() {
		mTimer.cancel();
		mTimer.purge();
		mTimer = null;
		mTimerTask = null;
		mListener = null;
		mFlag = false;
	}

	/**
	 * 任务是否在进行中
	 * */
	public boolean isRun() {
		return mFlag;
	}

	public interface OnTimeListener {
		public void onTimer();
	}
}
