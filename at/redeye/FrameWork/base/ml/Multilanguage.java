/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.ml;

import java.util.Collection;

/**
 *
 * @author martin
 */
public abstract class Multilanguage {

    Collection<MLComponent> components;
    
    /**
     * Register a Component to be notified when a
     * language is changed
     * @param component
     */
    public abstract void registerComponent( MLComponent component );
    
    /**
     * Deregister a Component from the notification list
     * required eg: before destroying a GUI label
     * @param component
     */
    public abstract void deregisterComponent( MLComponent component );
    
    /**
     * Sets the current language and notify all registered components
     * for a change.
     * @param Language
     * @return true if the language is available
     */
    public abstract boolean setCurrentLanguage( String Language );
    
    public abstract String getCurrentLanguage();
    
    /**
     * returns a list of languages, that are translation is available for
     * @return
     */
    public abstract Collection<String> getSupportedLanguages();
    
    /**
     * returns a translation for a specific component
     * @param System eg: GUI or BG
     * @param Type eg: Label, Menuitem, Dialog Title...
     * @param Orig language of the application
     * @return a translation if one is available
     */
    public abstract String getTranslationFor( String System, String Type, String Orig );
    
    
    /**
     * rereads all languages from Database, or elsewhere
     */
    public abstract void refresh();
}
