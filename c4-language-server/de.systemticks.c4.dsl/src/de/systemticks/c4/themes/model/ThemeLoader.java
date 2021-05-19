package de.systemticks.c4.themes.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

public class ThemeLoader {

    Gson gson = new Gson();
	
	public Theme loadFromFile(File file) {
		
        try {
			String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			return loadFromString(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Theme loadFromURL(URI uri) {
		return null;
	}	
	
	public Theme loadFromString(String json) {
		
		return gson.fromJson(json, Theme.class);
		
	}
	
	public Theme loadFromURL(URL url) {
		
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
			    String content = new BufferedReader(
			    	      new InputStreamReader(inputStream, StandardCharsets.UTF_8))
			    	        .lines()
			    	        .collect(Collectors.joining("\n"));
			   return loadFromString(content);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Map<String, ThemeElement> toMap(Theme theme) {
		
		HashMap<String, ThemeElement> result = new HashMap<String, ThemeElement>();
		
		theme.getElements().forEach( e -> result.put(e.getTag(), e));
		
		return result;
	}
	
}
