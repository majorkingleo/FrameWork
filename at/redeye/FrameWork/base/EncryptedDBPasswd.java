/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.utilities.DesEncrypt;

/**
 *
 * @author martin
 */
public class EncryptedDBPasswd
{
   public static String encryptDBPassword( final String DBPasswd, final String password )
   {
       final StringBuffer buf = new StringBuffer();

       AutoLogger al = new AutoLogger(EncryptedDBPasswd.class.getName())
       {
           public void do_stuff() throws Exception
           {
                DesEncrypt cipher = new DesEncrypt( password );
                String str = cipher.encrypt( DBPasswd );

                buf.append(str);
           }
       };

       if( al.isFailed() )
           return null;

       return buf.toString();
   }

   public static String decryptDBPassword(final String DBPasswd, final String password )
   {
       final StringBuffer buf = new StringBuffer();

       AutoLogger al = new AutoLogger(EncryptedDBPasswd.class.getName())
       {
           public void do_stuff() throws Exception
           {
                DesEncrypt cipher = new DesEncrypt( password );
                String str = cipher.decrypt(DBPasswd);

                buf.append(str);
           }
       };

       if( al.isFailed() )
           return null;

       return buf.toString();
   }

   public static String tryDecryptDBPassword(String DBPasswd, String password )
   {
       String res = decryptDBPassword(DBPasswd, password);

       if( res == null )
           return DBPasswd;

       return res;
   }

}
