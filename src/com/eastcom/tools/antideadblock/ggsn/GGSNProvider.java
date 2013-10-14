/**
 * GGSNProvider.java was created on 2013年7月30日 下午10:31:28
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.ggsn;

import java.util.List;

/**
 * @author sqwen
 */
public interface GGSNProvider {

    List<GGSN> findAll();

    GGSN findByName(String name);

	GGSN findByGtpCAddress(String address);

	GGSN findById(String id);

	void add(GGSN ggsn);
	void delete(GGSN ggsn);
	void update(GGSN ggsn);

}
