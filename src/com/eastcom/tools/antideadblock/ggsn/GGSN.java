/**
 * GGSN.java was created on 2013年7月28日 下午3:21:52
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.ggsn;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * @author sqwen
 */
public class GGSN {
	private String id;

	private String pool;

    private String name;

    private String type;

    private String protocol = "telnet";

    private String host;

    private int port = 23;

    private String username;

    private String password;

    private String logdir="../logs";

	private String omAddress;
	private String gtpCAddress;
	private String cmWapPool;
	private String cmNetPool;
	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GGSN other = (GGSN) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getOmAddress() {
		return omAddress;
	}

	public void setOmAddress(String omAddress) {
		this.omAddress = omAddress;
	}

	public String getGtpCAddress() {
		return gtpCAddress;
	}

	public void setGtpCAddress(String gtpCAddress) {
		this.gtpCAddress = gtpCAddress;
	}

	public String getCmWapPool() {
		return cmWapPool;
	}

	public void setCmWapPool(String cmWapPool) {
		this.cmWapPool = cmWapPool;
	}

	public String getCmNetPool() {
		return cmNetPool;
	}

	public void setCmNetPool(String cmNetPool) {
		this.cmNetPool = cmNetPool;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getLogdir() {
        return logdir;
    }

    public void setLogdir(String logdir) {
        this.logdir = logdir;
    }

}
