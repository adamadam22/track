package com.track.backend.utilities;


import java.util.Date;

import javax.xml.bind.DatatypeConverter;


public class DateParser {
	
	public static Date parseISO8601(String ISO8601String){
		return DatatypeConverter.parseDateTime(ISO8601String).getTime();
	}



}
