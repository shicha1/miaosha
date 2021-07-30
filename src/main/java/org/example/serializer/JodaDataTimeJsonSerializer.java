package org.example.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import java.io.IOException;

public class JodaDataTimeJsonSerializer extends JsonSerializer<DateTime> {
    @Override
    public void serialize(DateTime dataTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
         jsonGenerator.writeString(dataTime.toString("yyyy-MM-dd HH:mm:ss"));
    }
}
