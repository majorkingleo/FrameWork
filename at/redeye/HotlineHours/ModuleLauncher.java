package at.redeye.HotlineHours;

import at.redeye.FrameWork.base.DBConnection;
import org.apache.log4j.Logger;

import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.chart.impl.CategoryChartData;
import at.redeye.FrameWork.base.prm.impl.PrmDBInit;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ModuleLauncher {

    private static Root root = new LocalRoot("Bereitschaft-Stundenauswertung");
    private static Logger logger = Logger.getRootLogger();

    protected void invoke() {

        ConnectionDefinition conndef =
                new ConnectionDefinition("sauxdb2.salomon.at", "dsw", "dsw", "SADMIN",
                MOMMSupportedDBMSTypes.DB_ORACLE);
        DBConnection conn = new DBConnection(conndef);
        root.setDBConnection(conn);
        configureLogging();

        new MainWin(root).setVisible(true);
    }

    private void configureLogging() {

        PatternLayout layout = new PatternLayout(
                "%d{ISO8601} %-5p (%F:%L): %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);

        String logFileDir = root.getSetup().getLocalConfig(
                AppConfigDefinitions.LoggingDir);
        System.out.println("logFileDir: " + logFileDir);
        String logFileLevel = root.getSetup().getLocalConfig(
                AppConfigDefinitions.LoggingLevel);
        String loggingEnabled = root.getSetup().getLocalConfig(
                AppConfigDefinitions.DoLogging);

        String filename = logFileDir + (logFileDir.isEmpty() ? "" : "/") + "log.OS-" + System.getProperty("user.name", "unknown-user") + ".txt";

        System.out.println("Filename: " + filename);

        logger.setLevel(Level.toLevel(logFileLevel));
        logger.addAppender(consoleAppender);

        if (loggingEnabled.equalsIgnoreCase("ja") ||
                loggingEnabled.equalsIgnoreCase("yes") ||
                loggingEnabled.equalsIgnoreCase("true")) {

            try {

                RollingFileAppender fileAppender = new RollingFileAppender(
                        layout, filename);
                fileAppender.setAppend(true);
                fileAppender.setMaxFileSize("3MB");
                fileAppender.setName(RollingFileAppender.class.getSimpleName());

                logger.addAppender(fileAppender);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        StringUtils.autoLineBreak("Das Logger konnte nicht korrekt initialisiert werden!"),
                        "User Management", JOptionPane.WARNING_MESSAGE);

            }
        }

    }
}
