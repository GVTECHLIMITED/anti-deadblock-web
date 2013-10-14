/**
 * JsonGGSNProvider.java was created on 2013年7月30日 下午10:32:05
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.ggsn;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqwen
 */
//@Component
public class JsonGGSNProvider implements GGSNProvider {

    private List<GGSN> ggsns;

    private Map<String, GGSN> map;

    @Autowired
    public JsonGGSNProvider(@Value("${ggsn_file}") String fileName) {
        try {
            //String content = Files.toString(new File(fileName), Charset.defaultCharset());
            String content = Files.toString(new File(fileName), Charset.forName("utf-8"));
            System.out.println(Charset.defaultCharset());
            ggsns = JSON.parseArray(content, GGSN.class);
            map = new HashMap<String, GGSN>(ggsns.size());
            for (GGSN ggsn : ggsns) {
                map.put(ggsn.getName(), ggsn);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(fileName + " does not exist.", e);
        }
    }

    public List<GGSN> findAll() {
        return ggsns;
    }

    public GGSN findByName(String name) {
        return map.get(name);
    }

	@Override
	public GGSN findByGtpCAddress(String address) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public GGSN findById(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void add(GGSN ggsn) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void delete(GGSN ggsn) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void update(GGSN ggsn) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

}
