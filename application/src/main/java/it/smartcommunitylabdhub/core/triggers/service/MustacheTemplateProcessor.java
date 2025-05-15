/**
 * Copyright 2025 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.smartcommunitylabdhub.core.triggers.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MustacheTemplateProcessor implements TemplateProcessor {

    private static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};

    private final MustacheFactory factory = new DefaultMustacheFactory();

    @Override
    public Map<String, Serializable> process(Map<String, Serializable> template, Map<String, Serializable> data)
        throws IOException {
        if (template == null || template.isEmpty() || data == null || data.isEmpty()) {
            return template;
        }

        //convert to json to build template
        String json = JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(template);

        //process template
        StringWriter writer = new StringWriter();
        Mustache mustache = factory.compile(new StringReader(json), "template");
        mustache.execute(writer, data);
        writer.flush();

        //convert back to map
        String result = writer.toString();
        Map<String, Serializable> map = JacksonMapper.CUSTOM_OBJECT_MAPPER.readValue(result, typeRef);

        return map;
    }
}
