package com.dmware.api_onibusbh.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class TrimStringDeserializer extends JsonDeserializer<String> {

      @Override
      public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String value = p.getText();
            return value != null ? value.trim() : null;
      }
}
