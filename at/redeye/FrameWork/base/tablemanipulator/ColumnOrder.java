/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.tablemanipulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class ColumnOrder implements Comparator
{
    private static final Logger logger = Logger.getLogger(ColumnOrder.class.getName());
    
    static class Order
    {
        String name;
        int position_now;
        int position_wanted;
        
        public Order( String name, int position_now, int position_wanted )
        {
            this.name = name;
            this.position_now = position_now;
            this.position_wanted = position_wanted;
        }         
        
        @Override
        public String toString()
        {
            return String.format("%d soll %d %s", position_now, position_wanted, name );
        }
        
        boolean isOnWantedPosition()
        {
            if( position_wanted == -1 )
                return true;
            
            return position_now == position_wanted;
        }
    }
    
    JTable table;        
    ArrayList<Order> order_list;
    
    public ColumnOrder( JTable table )
    {
        this.table = table;     
        this.order_list = new ArrayList();
    }
    
    void addColumn( String name, int position_now, int position_wanted )
    {
        if( position_wanted >= table.getColumnCount() )
            position_wanted = -1;
        
        order_list.add(new Order(name, position_now, position_wanted) );
    }
    
    private void sort()
    {
        Collections.sort(order_list, this);
        
        if (logger.isDebugEnabled()) 
        {
            for (Order order : order_list) {
                logger.debug(order);
            }
        }
    }
    
    @Override
    public int compare(Object o1, Object o2) {
        
        Order order1 = (Order) o1;
        Order order2 = (Order) o2;
        
        if( order1.position_now == order2.position_now )        
            return 0;
        else if( order1.position_now < order2.position_now )
            return -1;
        else
            return 1;
    }    
    
    void moveColumns()
    {
        boolean needs_sorting = false;
        
        final TableColumnModel model = table.getColumnModel();
        final int columns = table.getColumnCount();
        
        do
        {
            needs_sorting = false;
            
            sort();
                        
            for( Order order : order_list )
            {
                if( !order.isOnWantedPosition() )
                {                                       
                    logger.debug( " => " + order );
                    model.moveColumn(order.position_now, order.position_wanted);
                    
                    // alle Spalten, die dahinter liegen eine position runterzählen
                    for( int i = order.position_now + 1; i < columns && i < order_list.size(); i++ )
                    {
                        Order o = order_list.get(i);                            
                        o.position_now--;                        
                    }
                    
                    // und jetzt alle Spalten die hinter der Einfügepositin liegen eine Spalte hinaufzählen
                    for( int i = order.position_wanted + 1; i < columns && i < order_list.size(); i++ )
                    {
                        Order o = order_list.get(i);                            
                        o.position_now++;                        
                    }
                    
                    order.position_now = order.position_wanted;
                    break;
                }                
            }

            
            for( Order order : order_list )
            {
                if( !order.isOnWantedPosition() )
                    needs_sorting = true;
            }
            

        } while( needs_sorting );
    }
    
}
