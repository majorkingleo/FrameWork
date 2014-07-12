/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.utilities;

import java.io.InputStream;

public class ThreadReader extends Thread {

    Process process;
    StringBuilder builder;
    StringBuilder err_builder;
    boolean done;
    int result = 0;

    public ThreadReader(Process process) {
        this.process = process;
        builder = new StringBuilder();
        err_builder = new StringBuilder();
        done = false;
    }

    @Override
    public void run() {

        try {
            InputStream stream = process.getInputStream();
            InputStream err_stream = process.getErrorStream();

            int len;

            Thread wait_thread = new Thread() {

                @Override
                public void run() {
                    try {
                        result = process.waitFor();
                    } catch (InterruptedException ex) {
                    }
                }
            };

            wait_thread.start();

            do {
                sleep(100);

                while ((len = stream.available()) > 0) {
                    byte bytes[] = new byte[len];

                    len = stream.read(bytes);

                    byte bytes2[] = new byte[len];

                    System.arraycopy(bytes, 0, bytes2, 0, len);

                    builder.append(new String(bytes, "UTF-8"));
                }

                while ((len = err_stream.available()) > 0) {
                    byte bytes[] = new byte[len];

                    len = err_stream.read(bytes);

                    byte bytes2[] = new byte[len];

                    System.arraycopy(bytes, 0, bytes2, 0, len);

                    err_builder.append(new String(bytes, "UTF-8"));
                }

            } while (wait_thread.isAlive());
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
    }

    public void stopReading() {
        done = true;
    }

    public String getResult() {
        return builder.toString();
    }

    public String getErrResult() {
        return err_builder.toString();
    }
    
   public int getReturnValue()
    {
        return result;
    }    
}
