package com.track.backend.json.serializers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.stereotype.Component;

/*
 * chage serialization way of java.util.Date
 * to ISO8601 Instant Date Format
 * */
@Component
public class JsonIso8601Serializer extends JsonSerializer<Date> {
	  
	@Override
	public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider)
	  throws IOException, JsonProcessingException {
		ZonedDateTime iso8601Date = ZonedDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
		String stringIso8601Date = iso8601Date.toInstant().toString();
	    jgen.writeString(stringIso8601Date);
	  }
	
}
