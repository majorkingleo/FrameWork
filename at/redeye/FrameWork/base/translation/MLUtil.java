/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.translation;

import at.redeye.FrameWork.base.Root;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

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

    public static String getAltResourcePath(String resourceName, String subdir)
    {
        int index = resourceName.lastIndexOf('/');

        return resourceName.substring(0,index) + '/' + subdir + resourceName.substring(index);
    }

    /**
     * Adds all Properties from b to a
     * @param a
     * @param b
     */
    public static void addAllProps( Properties a , Properties b )
    {
        Set<Object> keys = b.keySet();

        Iterator<Object> it = keys.iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            String val = (String) b.get(key);

            if (!val.isEmpty()) {
                a.setProperty(key, val);
            }
        }
    }

    private static boolean haveResource( String name )
    {
        //System.out.println("testing: " + name);

        URL url = name.getClass().getResource(name);

        if( url != null )
            return true;

        return false;
    }

    public static Properties autoLoadFile4Class(Root root, Object object, String locale, boolean no_default)
    {
        try {
            return loadFile4Class(root, object, locale, no_default);
        } catch( FileNotFoundException ex ) {
            return null;
        } catch( IOException ex ) {
            return null;
        }
    }

    private static Properties loadFile4Class(Root root, Object object, String locale, boolean no_default) throws FileNotFoundException, IOException
    {
        Properties p = null;

        p = loadFile4ClassInt(root, object, locale);

        if( p != null )
            return p;

        String parts[] = locale.split("_");

        if ( parts.length == 1 ) {
            if( !no_default )
                p = loadFile4ClassInt(root, object,root.getDefaultLanguage());
        }

        if( p != null )
            return p;

        p = loadFile4ClassInt(root, object, parts[0]);

        if( p != null )
            return p;

        if( !no_default )
            p = loadFile4ClassInt(root, object,root.getDefaultLanguage());

        return p;
    }

    private static Properties loadFile4ClassInt( Root root, Object object, String lang ) throws FileNotFoundException, IOException
    {
        String dir = TranslationDialog.getTranslationsDir(root);

        String file_name =  "/" + object.getClass().getName();

        String base_name = dir + file_name;
        String prop = ".properties";

        String extra = "_";

        if( lang.isEmpty() )
            extra = "";

        File dir_exact = new File( base_name + extra + lang + prop );

        String resource_name = "/" + object.getClass().getName().replaceAll("\\.", "/") + extra + lang + prop;

        String alt1_resource_name = "/" + MLUtil.getAltResourcePath(object.getClass().getName().replaceAll("\\.", "/"), "translations") + extra + lang + prop;
        String alt2_resource_name = "/" + MLUtil.getAltResourcePath(object.getClass().getName().replaceAll("\\.", "/"), "resources/translations") + extra + lang + prop;

        Properties local_props = new Properties();

         boolean not_found = false;

        if( dir_exact.isFile() )
        {
            FileInputStream in = new FileInputStream(dir_exact);
            local_props.load(in);
            in.close();

        } else if( haveResource( resource_name ) ) {

            InputStream in = root.getClass().getResourceAsStream( resource_name );
            local_props.load( in );
            in.close();

        } else if( haveResource( alt1_resource_name ) ) {

            InputStream in = root.getClass().getResourceAsStream( alt1_resource_name );
            local_props.load( in );
            in.close();

        } else if( haveResource( alt2_resource_name ) ) {

            InputStream in = root.getClass().getResourceAsStream( alt2_resource_name );
            local_props.load( in );
            in.close();
        } else {
             local_props = null;
        }

         return local_props;
    }

    public static Properties autoLoadFile4Class(Root root, Object object, String locale, String impl_language)
    {
        try {
            boolean no_default = false;

            if( impl_language.equals(locale) )
                no_default = true;
            else if( locale.startsWith(impl_language) )
                no_default = true;

            return loadFile4Class(root, object, locale, no_default);
        } catch( FileNotFoundException ex ) {
            return null;
        } catch( IOException ex ) {
            return null;
        }
    }
}
