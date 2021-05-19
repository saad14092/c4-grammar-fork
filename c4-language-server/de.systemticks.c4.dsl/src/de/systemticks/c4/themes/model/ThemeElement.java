package de.systemticks.c4.themes.model;

import com.google.gson.annotations.Expose;

public class ThemeElement {

	@Expose
	private String tag;
	@Expose
	private String stroke;
	@Expose
	private String color;
	@Expose
	private String icon;
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public String getStroke() {
		return stroke;
	}
	
	public void setStroke(String stroke) {
		this.stroke = stroke;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}	
	
}
