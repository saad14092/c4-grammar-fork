package de.systemticks.c4.themes.model;

import java.util.List;

import com.google.gson.annotations.Expose;

public class Theme {

	@Expose
	private String name;
	@Expose
	private String description;
	@Expose
	private List<ThemeElement> elements;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<ThemeElement> getElements() {
		return elements;
	}
	
	public void setElements(List<ThemeElement> elements) {
		this.elements = elements;
	}
	
	
}
