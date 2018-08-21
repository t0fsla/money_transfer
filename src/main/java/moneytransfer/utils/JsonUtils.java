package moneytransfer.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import spark.ResponseTransformer;

public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new JodaMoneyModule());
        mapper.registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static ResponseTransformer json() {
        return JsonUtils::toJson;
    }
}
