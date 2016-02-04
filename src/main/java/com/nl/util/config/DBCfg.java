package com.nl.util.config;


import java.io.Serializable;

/**
 * Created by gukf on 2014-5-22.
 */
public class DBCfg implements Serializable, Comparable<DBCfg> {
    private String driver;
    private String url;
    private String user;
    private String password;
    private String dbName;
    private String dbType;
    private Double dbVersion;
    private String dbBak;
    private String security;
    private String keytab;
    private String principal;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Double getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(Double dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getDbBak() {
        return dbBak;
    }

    public void setDbBak(String dbBak) {
        this.dbBak = dbBak;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getKeytab() {
        return keytab;
    }

    public void setKeytab(String keytab) {
        this.keytab = keytab;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return "DBCfg{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbType='" + dbType + '\'' +
                ", dbVersion=" + dbVersion +
                ", dbBak='" + dbBak + '\'' +
                ", security='" + security + '\'' +
                ", keytab='" + keytab + '\'' +
                ", principal='" + principal + '\'' +
                '}';
    }

    public int compareTo(DBCfg o) {
        if (this.driver.equals(o.getDriver())
                && this.url.equals(o.getUrl())
                && this.user.equals(o.getUser())
                && this.password.equals(o.getPassword())
                && this.dbName.equals(o.getDbName())
                && this.dbType.equals(o.getDbType())
                && this.dbVersion.equals(o.getDbVersion())
                && this.security.equals(o.getSecurity())
                && this.keytab.equals(o.getKeytab())
                && this.principal.equals(o.getPrincipal())
                ) {
            return 0;
        } else {
            return -1;
        }
    }
}
