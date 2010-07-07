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
import at.redeye.FrameWork.base.imagestorage.ImageUtils;
import java.awt.Dimension;

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
    public DBInteger width = new DBInteger("width", "Breite");
    public DBInteger height = new DBInteger("height", "Höhe");
    
    public DBImage()
    {
        super("IMAGES");
        
        add(id);
        add(image);
        add(file_name);
        add(hist);
        add(width,2);
        add(height,2);
        
        id.setAsPrimaryKey();

        setVersion(2);
    }

    /**
     * Loads an Imager from a byte stream. Automatically calculates width and height.
     * The id has to be filled by you
     * @param bytes
     * @param descr
     * @param user
     */
    public void loadContent( byte bytes[], String descr, String user )
    {
        Dimension dim = ImageUtils.calcDimensions( bytes, descr );
        width.loadFromCopy((Integer)dim.width);
        height.loadFromCopy((Integer)dim.height);
        hist.setAnHist(user);
        image.value = bytes;
    }

    public int getWidth()
    {
        return width.getValue();
    }

    public int getHeight()
    {
        return height.getValue();
    }
    
    @Override
    public DBStrukt getNewOne() {
        return new DBImage();
    }

}
