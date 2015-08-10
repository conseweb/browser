package com.universe.galaxy.version;

import java.io.Serializable;

public class VersionInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	private String version;
	private String size;
	private String text;
	private String path;
	private String vtime;

	public VersionInfo(String title, String version, String size, String text,
			String path, String vtime) {
		this.title = title;
		this.version = version;
		this.size = size;
		this.text = text;
		this.path = path;
		this.vtime = vtime;
	}

	public VersionInfo(String title, String version, String size, String text,
			String path) {
		this.title = title;
		this.version = version;
		this.size = size;
		this.text = text;
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getVtime() {
		return vtime;
	}

	public void setVtime(String Vtime) {
		this.vtime = Vtime;
	}
}
