package zems.core.properties.value;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import zems.core.concept.Properties;
import zems.core.concept.Value;
import zems.core.properties.InMemoryProperties;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@JsonComponent
public class ValueJsonSupport {

    public static class PropertiesSerializer extends JsonSerializer<Properties> {
        @Override
        public void serialize(Properties properties, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            for (String key : properties.keys()) {
                final Optional<Value<?>> value = properties.find(key);
                if (value.isPresent()) {
                    jsonGenerator.writeFieldName(key);
                    jsonGenerator.writeObject(value.get());
                }
            }
            jsonGenerator.writeEndObject();
        }
    }

    public static class PropertiesDeserializer extends JsonDeserializer<Properties> {

        @Override
        public Properties deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            Map<String, Object> data = jsonParser.readValueAs(new TypeReference<>() {
            });
            return InMemoryProperties.from(data);
        }
    }

    public static class ListValueSerializer extends JsonSerializer<ListValue> {
        @Override
        public void serialize(ListValue listValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartArray();
            for (Value<?> v : listValue.value()) {
                if (v instanceof ReferenceValue) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeFieldName("loadFrom");
                    jsonGenerator.writeObject(v);
                    jsonGenerator.writeEndObject();
                } else {
                    jsonGenerator.writeObject(v);
                }
            }
            jsonGenerator.writeEndArray();
        }
    }

    public static class GenericValuesSerializer extends JsonSerializer<Value<?>> {
        @Override
        public void serialize(Value<?> value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(value.value());
        }
    }
}
