/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年7月28日 下午10:05:40
* @Description: 无
*/
package com.nl.util.hdfs.format;

import java.io.Serializable;

/**
 * Formatter interface for determining HDFS file names.
 *
 */
public interface FileNameFormat extends Serializable {

   
    String getName(String date);
    String getPath();
}
