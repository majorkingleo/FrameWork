/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.imagestorage;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author martin
 */
public class ImageCellRenderer implements ListCellRenderer {

    private static Color highlightColor = new Color(184,207,229);
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = (Component) value;
        component.setBackground(isSelected ? highlightColor : Color.white);
        component.setForeground(isSelected ? Color.white : highlightColor);
        return component;
    }
}
