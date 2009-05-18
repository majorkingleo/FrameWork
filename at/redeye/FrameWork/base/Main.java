package at.redeye.FrameWork.base;



/**
 *
 * @author martin
 */
public class Main {

    public static void main(String[] args) {
        // TODO code application logic here                
        
         java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Root root = new LocalRoot( "Framework" );
                
                try {
                    root.loadDBConnectionFromSetup();
                } catch( NoClassDefFoundError ex ) {
                    System.out.println(ex);
                }   
                new RootWin( root ).setVisible(true);
            }
        });
    }

}
