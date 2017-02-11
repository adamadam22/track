package com.track.backend.utilities;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;



@JsonSerialize
public class JsonableList <T> {
	private List<T> list;

	private JsonableList(){};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JsonableList fromList(List list){
		JsonableList jsonableList = new JsonableList();
		jsonableList.list=list;
		return jsonableList;
	}
	
	public List<T> getList() {
		return list;
	}
	
	@XmlElement
	public void setList(List<T> list) {
		this.list = list;
	}
	
	

}
