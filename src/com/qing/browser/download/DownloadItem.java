package com.qing.browser.download;

public class DownloadItem {
	private int downloadID;
	private int startPos;// 开始点
	private int endPos;// 结束点
	private int compeleteSize;// 完成度
	private String url;// 下载器网络标识
	private int progress;
	private String mErrorMessage;
	private int downloadState;
	private String mFileName;

	public DownloadItem() {

	}

	public DownloadItem(int downloadId, int startPos, int endPos,
			int compeleteSize, String url, int state, String filename) {
		super();
		this.downloadID = downloadId;
		this.startPos = startPos;
		this.endPos = endPos;
		this.compeleteSize = compeleteSize;
		this.url = url;
		this.setDownloadState(state);
		this.mFileName = filename;
	}

	public void setFileName(String filename) {
		this.mFileName = filename;
	}

	public String getFileName() {
		return mFileName;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public int getCompeleteSize() {
		return compeleteSize;
	}

	public void setCompeleteSize(int compeleteSize) {
		this.compeleteSize = compeleteSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progess) {
		this.progress = progess;
	}

	public void setErrorMessage(String errorMessage) {
		mErrorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	@Override
	public String toString() {
		return " startPos=" + startPos + ", endPos=" + endPos
				+ ", compeleteSize=" + compeleteSize + " progress=" + progress
				+ " state=" + downloadState + " mFileName=" + mFileName;
	}

	public int getdownloadID() {
		return downloadID;
	}

	public void setdownloadID(int id) {
		this.downloadID = id;
	}

	public int getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}

}
