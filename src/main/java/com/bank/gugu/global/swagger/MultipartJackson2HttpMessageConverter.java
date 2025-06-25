package com.bank.gugu.global.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 * Swagger에서 multipart/form-data와 application/json 타입을 같이 사용하기 위해
 */
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    /**
     * Converter for support http request with header Content-Type: multipart/form-data
     * swagger에서 json,multiPartFile 객체를 한번에 요청시 Multipart/form-data 이하의 application/json 타입을 누락
     * -> application/octet-stream 타입을 convert
     */
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}
