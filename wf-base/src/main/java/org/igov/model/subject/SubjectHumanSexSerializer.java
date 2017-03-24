package org.igov.model.subject;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class SubjectHumanSexSerializer extends JsonSerializer<SubjectHumanSex> {

	@Override
	public void serialize(SubjectHumanSex value, JsonGenerator generator,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		// output the custom Json
		generator.writeStartObject();

		// the type
		generator.writeFieldName("nID_Sex");
		generator.writeString(value.getnID_Sex());

		generator.writeFieldName("sID_Sex");
		generator.writeString(value.getsID_Sex());
		// end tag
		generator.writeEndObject();

	}
}
