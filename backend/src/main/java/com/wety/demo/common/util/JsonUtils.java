package com.wety.demo.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Json-Java对象之间序列化/反序列化工具类
 * <p>
 * 该工具类依赖jackson类库完成底层的序列化/反序列化
 *
 * @author wety
 */
public class JsonUtils {

    private JsonUtils() {
    }

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将json字符串反序列化为java对象
     *
     * @param <T>        java对象类型
     * @param jsonString json字符串内容
     * @param valueType  T.class, java对象类型Class实例
     *
     * @return T类型的对象<br> 或null，如果jsonString为空
     *
     * @throws Exception JsonParseException, JsonMappingException, IOException
     */
    public static <T> T readValue(String jsonString, Class<T> valueType) throws JsonProcessingException {
        if (jsonString == null || jsonString.isEmpty()) {
            log.warn("jsonString is empty, null will be returned!");
            return null;
        } else {
            return OBJECT_MAPPER.readValue(jsonString, valueType);
        }
    }

    public static <T> T readValue(String jsonString, TypeReference<T> typeReference) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonString, typeReference);
    }

    /**
     * 将对象序列化为json字符串
     *
     * @param value 被序列化的java对象
     *
     * @return value序列化后的json字符串<br> 或空字符串如果value为null
     *
     * @throws JsonProcessingException
     */
    public static String writeValueAsString(Object value) throws JsonProcessingException {
        if (value == null) {
            log.warn("value is null, empty string will returned!");
            return "";
        } else {
            return OBJECT_MAPPER.writeValueAsString(value);
        }
    }

    /**
     * 将对象序列化为json字符串并以UTF-8编码写入OutputStream<br> 注意：该方法不会关闭outputStream，调用者需要自行关闭
     *
     * @param outputStream
     * @param value
     *
     * @throws Exception
     */
    public static void writeValue(OutputStream outputStream, Object value) throws IOException {
        if (value == null) {
            log.warn("value is null, nothing will be written to the outputStream!");
        } else {
            OBJECT_MAPPER.writeValue(outputStream, value);
        }
    }

    /**
     * 将对象序列化为json字符串并以UTF-8编码写入OutputStream<br> 该方法会把json数据中的"和\去掉
     * 注意：该方法不会关闭outputStream，调用者需要自行关闭
     *
     * @param data
     * @param outputStream 需要调用者自行关闭
     *
     * @throws IOException
     */
    public static void populateJson(Object data, OutputStream outputStream) throws IOException {
        if (data == null) {
            OBJECT_MAPPER.writeValue(outputStream, null);
        } else if (data instanceof String) {
            // If data is already a string, process it according to the original logic
            String jsonResult = (String) data;
            if (jsonResult.startsWith("\"")) {
                jsonResult = jsonResult.substring(1);
                jsonResult = jsonResult.substring(0, jsonResult.length()-1);
                jsonResult = jsonResult.replace("\\", "");
            }
            OBJECT_MAPPER.writeValue(outputStream, jsonResult);
        } else {
            // If data is an object, serialize it directly to the output stream without the extra escaping processing
            OBJECT_MAPPER.writeValue(outputStream, data);
        }
    }
}
