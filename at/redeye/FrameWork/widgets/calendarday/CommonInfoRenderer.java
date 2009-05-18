/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.widgets.calendarday;

import org.joda.time.DateMidnight;

/**
 *
 * @author martin
 */
public class CommonInfoRenderer implements InfoRenderer 
{
    protected StringBuilder info = new StringBuilder();
    
    public void clear() {
         info.delete(0, info.length());
    }

    public void setInfo(String info) {
        this.info.append(info);
    }

    public void update() {
        // nix zu tun
    }

    public String render() {
        return "<html><body><font size=\"2\">" + info.toString() + "</font></body></html>";
    }

    public void addContent(Object data) {
        info.append(data);
    }

    public InfoRenderer getNewInstance() {
        return new CommonInfoRenderer();
    }

    public void setDay(DateMidnight day) {
        // brauch ma net
    }

    public String renderSum()
    {
        return "";
    }
}
