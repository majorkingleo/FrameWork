/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.ml;

/**
 *
 * @author martin
 */
public abstract class MLComponent {
    
    Multilanguage ml;
    String System;
    String Type;
    String Orig;
    
    public MLComponent( Multilanguage ml, String System, String Type, String Orig )
    {
        this.ml = ml;
        this.System = System;
        this.Type = Type;
        this.Orig = Orig;
    }
    
    abstract void setTranslation( String trans );             
    
    void notifyLanguageChange()
    {
        String Trans = ml.getTranslationFor(System, Type, Orig);
        
        if( !Trans.isEmpty() )
            setTranslation( Trans );
    }        
}
