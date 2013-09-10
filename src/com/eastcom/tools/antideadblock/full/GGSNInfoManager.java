package com.eastcom.tools.antideadblock.full;

import com.eastcom.tools.antideadblock.ggsn.GGSNProvider;
import com.eastcom.tools.antideadblock.pool.GGSNSessionFactoryImpl;
import com.eastcom.tools.antideadblock.pool.GGSNSessionPoolFactory;
import com.eastcom.tools.antideadblock.template.ExecTemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-8-23
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
@Component("ggsnInfoManager")
public class GGSNInfoManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private GGSNSessionPoolFactory sessionPoolFactory;

	@Autowired
	private ExecTemplateProvider execTemplateProvider;

	@Autowired
	private GGSNProvider ggsnProvider;

    @Autowired
    private GGSNSessionFactoryImpl ggsnSessionFactory;

    public GGSNSessionFactoryImpl getGgsnSessionFactory() {
        return ggsnSessionFactory;
    }

    public void setGgsnSessionFactory(GGSNSessionFactoryImpl ggsnSessionFactory) {
        this.ggsnSessionFactory = ggsnSessionFactory;
    }

    public GGSNSessionPoolFactory getSessionPoolFactory() {
		return sessionPoolFactory;
	}

	public void setSessionPoolFactory(GGSNSessionPoolFactory sessionPoolFactory) {
		this.sessionPoolFactory = sessionPoolFactory;
	}

	public ExecTemplateProvider getExecTemplateProvider() {
		return execTemplateProvider;
	}

	public void setExecTemplateProvider(ExecTemplateProvider execTemplateProvider) {
		this.execTemplateProvider = execTemplateProvider;
	}

	public GGSNProvider getGgsnProvider() {
		return ggsnProvider;
	}

	public void setGgsnProvider(GGSNProvider ggsnProvider) {
		this.ggsnProvider = ggsnProvider;
	}
}
