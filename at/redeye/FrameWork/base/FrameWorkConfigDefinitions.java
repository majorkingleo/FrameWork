/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBConfig;

/**
 *
 * @author martin
 */
public class FrameWorkConfigDefinitions {

    public static DBConfig HelpParamColorEven = new DBConfig("HelpParamColorEven", "#fefeaa", "Hilfehintergundfarbe für Parameter gerade Zeilen.");
    public static DBConfig HelpParamColorOdd = new DBConfig("HelpParamColorOdd", "#ddeedd", "Hilfehintergundfarbe für Parameter ungerade Zeilen.");
    public static DBConfig HelpParamColorTitle = new DBConfig("HelpParamColorTitle", "#aaddff", "Hilfehintergundfarbe für Parameter Titel.");
    public static DBConfig SpreadSheetColorEven = new DBConfig("SpreadSheetColorEven", "#d2ebf5", "Hintergundfarbe der Tabelle bei geraden Reihen.");
    public static DBConfig SpreadSheetColorEvenEditable = new DBConfig("SpreadSheetColorEvenEditable", "#f5f5ff", "Hintergundfarbe der Tabelle bei geraden editiebaren Reihen.");
    public static DBConfig SpreadSheetColorOdd = new DBConfig("SpreadSheetColorOdd", "#ffffff", "Hintergundfarbe der Tabelle bei ungeraden Reihen.");
    public static DBConfig SpreadSheetColorOddEditable = new DBConfig("SpreadSheetColorOddEditable", "#dcf5eb", "Hintergundfarbe der Tabelle bei ungeraden editiebaren Reihen.");
    public static DBConfig SpreadSheetMarginEditable = new DBConfig("SpreadSheetMarginEditable", "20", "Zusätzlicher Rand, um den die Tabellenspalten breiter gemacht werden um besseres Editieren zu ermöglichen. Dies gilt nur für editierbare Spalten.");
    public static DBConfig SpreadSheetMarginReadOnly = new DBConfig("SpreadSheetMarginReadOnly", "5", "Zusätzlicher Rand, um den die Tabellenspalten breiter gemacht werden. Dieser Wert gilt nur für nicht editierbare Spalten.");
    public static DBConfig DefaultAutoLineBreakWidth = new DBConfig("DefaultAutoLineBreakWidth", "40", "Breite eines automatisch umgebrochenen Textes.");
    public static DBConfig ImagePreviewInFileOpen = new DBConfig("ImagePreviewinFileOpen", "false", "Soll im Datei öffnen Dialogen die Bildervorschau angezeigt werden?");
    public static DBConfig AllowAutoLogin = new DBConfig("AllowAutoLogin", "false", "Ist eine automatische Anmeldung generell zulässig?");
    public static DBConfig AutoLoginUser = new DBConfig("AutoLoginUser", "", "Legt den Login fest, mit dem die automatische Anmeldung durchgeführt wird.");
    

    public static void registerDefinitions() {
        GlobalConfigDefinitions.add_help_path("/at/momm/FrameWork/framework/resources/Help/Params/");
        LocalConfigDefinitions.add_help_path("/at/momm/FrameWork/framework/resources/Help/Params/");

        addLocal(HelpParamColorEven);
        addLocal(HelpParamColorOdd);
        addLocal(HelpParamColorTitle);

        addLocal(SpreadSheetColorEven);
        addLocal(SpreadSheetColorEvenEditable);
        addLocal(SpreadSheetColorOdd);
        addLocal(SpreadSheetColorOddEditable);
        addLocal(SpreadSheetMarginEditable);
        addLocal(SpreadSheetMarginReadOnly);

        addLocal(DefaultAutoLineBreakWidth);
        addLocal(ImagePreviewInFileOpen);

        addLocal(AutoLoginUser);
        

        add(AllowAutoLogin);
    }

    static void add(String name, String value, String descr) {
        GlobalConfigDefinitions.add(new DBConfig(name, value, descr));
    }

    static void add(DBConfig c) {
        GlobalConfigDefinitions.add(c);
    }

    static void addLocal(DBConfig c) {
        LocalConfigDefinitions.add(c);
    }
}
