/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author martin
 */


/**
 * This program reads a text file line by line and print to the console. It uses
 * FileOutputStream to read the file.
 * 
 */
public class ReadFile 
{

  public static StringBuilder read_file_builder(String file_name) 
  {

    File file = new File(file_name);
    FileReader fis = null;
    BufferedReader bis = null;
    
    StringBuilder res = new StringBuilder();
    
    try {
      fis = new FileReader(file);

      // Here BufferedInputStream is added for fast reading.
      bis = new BufferedReader(fis);
                  
      int len = 10;
      
      char buff[] = new char[(int)Math.min(len,file.length())];
      
      // dis.available() returns 0 if the file does not have more lines.
      while (bis.ready()) {                        
        len = bis.read(buff);
        
        res.append(buff, 0, len);
        //System.out.println(dis.readLine());
      }

      // dispose all the resources after using them.
      fis.close();
      bis.close();      

    } catch (FileNotFoundException e) {
      System.out.println("File Not Found: " + file_name);
    } catch (IOException e) {
      System.out.println(e);
      // e.printStackTrace();
    }
    
    return res;
  }    
  
  public static String read_file(String file_name)
  {
    return read_file_builder( file_name ).toString();
  }  
}