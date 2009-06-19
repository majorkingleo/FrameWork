/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.imagestorage.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBBlob;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author martin
 */
public class DBImage extends DBStrukt 
{
    public DBInteger id = new DBInteger("Id");
    public DBBlob image = new DBBlob("image");
    public DBString file_name = new DBString("file_name", "Dateiname", 255);
    public DBHistory hist = new DBHistory("hist");
    
    public DBImage()
    {
        super("IMAGES");
        
        add(id);
        add(image);
        add(file_name);
        add(hist);
        
        id.setAsPrimaryKey();
    }
    
    
    @Override
    public DBStrukt getNewOne() {
        return new DBImage();
    }

}
