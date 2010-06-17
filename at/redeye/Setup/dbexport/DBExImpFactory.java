/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.dbexport;

import at.redeye.FrameWork.base.Root;

/**
 *
 * @author martin
 */
public class DBExImpFactory
{
    public DatabaseImport getNewImporter( Root root, String source_file_name )
    {
        return new DatabaseImport( root, source_file_name );
    }

    public DatabaseExport getNewExporter( Root root, String target_file_name )
    {
        return new DatabaseExport( root, target_file_name );
    }
}
