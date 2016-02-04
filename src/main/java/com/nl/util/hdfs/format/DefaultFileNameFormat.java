/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年7月29日 上午10:42:56
* @Description: 无
*/
package com.nl.util.hdfs.format;

public class DefaultFileNameFormat implements FileNameFormat {
	

	private static final long serialVersionUID = 1L;
	private String path = "/gsw";
    private String prefix = "gsw";
    private String extension = ".gsw";

    /**
     * Overrides the default prefix.
     *
     * @param prefix
     * @return
     */
    public DefaultFileNameFormat withPrefix(String prefix){
        this.prefix = prefix;
        return this;
    }

    /**
     * Overrides the default file extension.
     *
     * @param extension
     * @return
     */
    public DefaultFileNameFormat withExtension(String extension){
        this.extension = extension;
        return this;
    }

    public DefaultFileNameFormat withPath(String path){
        this.path = path;
        return this;
    }


    @Override
    public String getName(String date) {
        return this.prefix  + date + this.extension;
    }

    public String getPath(){
        return this.path;
    }

}
