/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.imagestorage;

import at.redeye.FrameWork.base.imagestorage.bindtypes.DBImage;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class LeastTransparentImage
{
    public static class Content
    {
        DBImage image;
        Image img;
        int tranparency;

        public Content( DBImage db_image, Image img )
        {
            this.image = db_image;
            this.img = img;
            tranparency = 0;
        }
    }

    Component parent;
    MediaTracker tracker;
    ArrayList<Content> images;

    public LeastTransparentImage( Component parent )
    {
        this.parent = parent;
        tracker = new MediaTracker(parent);
        images = new ArrayList<Content>();
    }

    public void add( DBImage image )
    {
        Image img = java.awt.Toolkit.getDefaultToolkit().createImage(image.image.value);

        tracker.addImage(img, image.id.getValue());
        images.add(new Content(image,img));
    }

    public void addAll( Vector<DBImage> images )
    {
        for( DBImage image : images )
        {
            add( image );
        }
    }

    public Image getLeastTransparentImage()
    {
        try
        {
            tracker.waitForAll();
        } catch( InterruptedException ex ) {

        }

        for (Content entry: images) {

            int width = entry.img.getWidth(null);
            int height = entry.img.getHeight(null);

            int pixels[] = new int[width*height];

            PixelGrabber grabber = new PixelGrabber(entry.img, 0, 0,
                    width, height, pixels, 0, width);

            try {
                grabber.grabPixels();
            } catch (InterruptedException ex) {
                System.out.println("ex: " + ex);
            }

            for (int x = 0, y = 0; x < width && y < height; x++, y++) {

                int val = pixels[x * y];
                int alpha = (val >> 24) & 0xFF;

                System.out.println(String.format(entry.image.file_name.getValue() + " %X alpha %X",
                        val, alpha));

                entry.tranparency += alpha;
            }

        }

        int max = 0;
        Image max_img = null;

        for( Content entry: images )
        {
            if( entry.tranparency >= max )
            {
                max_img = entry.img;
                max = entry.tranparency;
            }
        }

        return max_img;
    }

}
