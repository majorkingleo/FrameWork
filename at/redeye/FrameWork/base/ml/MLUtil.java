/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.ml;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
 * @author martin
 */
public class MLUtil {

    public static Properties convertResourceBundleToProperties(ResourceBundle resource) {
        Properties properties = new Properties();

        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            properties.put(key, resource.getString(key));
            //System.out.println("key: '" + key + "' value: '" + properties.get(key) + "'");
        }

        return properties;
    }
}
