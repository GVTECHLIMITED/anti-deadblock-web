/**
 * JsonExecTemplateProvider.java was created on 2013年7月29日 下午5:58:53
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.template;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqwen
 */
@Component
public class JsonExecTemplateProvider implements ExecTemplateProvider {

    private Map<String, ExecTemplate> map;

    @Autowired
    public JsonExecTemplateProvider(@Value("${exec_template_file}") String fileName) {
        try {
            //String content = Files.toString(new File(fileName), Charset.defaultCharset());
	        File file = new File(getClass().getClassLoader().getResource(fileName).toURI());
	        String content = Files.toString(file, Charset.forName("utf-8"));
            List<ExecTemplate> list = JSON.parseArray(content, ExecTemplate.class);
            map = new HashMap<String, ExecTemplate>(list.size());
            for (ExecTemplate ctx : list) {
                map.put(ctx.getType(), ctx);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(fileName + " does not exist.", e);
        } catch (URISyntaxException e) {
	        throw new IllegalArgumentException(fileName + " does not exist.", e);
        }
    }

    public ExecTemplate getExecTemplate(String type) {
        return map.get(type);
    }

}
