package com.ft.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "backup.google")
@Data
public class GoogleApiConfig {

    @JsonProperty("private_key_id")
    private String privateKeyId;

    @JsonProperty("private_key")
    private String privateKey;

    @JsonProperty("client_email")
    private String clientEmail;

    @JsonProperty("client_id")
    private String clientId;

    private String type;

    private List<String> shareWithUsers;

    @Autowired @JsonIgnore private ObjectMapper objectMapper;

    public InputStream toInputStream() throws IOException {
        fixLineSeparator();
        Writer writer = new StringWriter();
        objectMapper.writeValue(writer, this);
        String json =  writer.toString();
        return new ByteArrayInputStream(json.getBytes());
    }

    private void fixLineSeparator() {
        if (privateKey.contains("\\n")) {
            privateKey = privateKey.replace("\\n", "\n") ;
        }
    }
}
