/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.autodialog.dbstrukt.impl;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import at.redeye.FrameWork.autodialog.dbstrukt.IDBStruktDialog;
import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.BasePanel;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.widgets.datetime.IDateTimeComponent;

/**
 * @author Mario Mattl
 */
public abstract class AbstractDbStruktDialog<T extends DBStrukt> extends
		BaseDialog implements IDBStruktDialog {

	private static final long serialVersionUID = 1L;

	private final static Font titleFont = new Font("Tahoma", Font.BOLD, 24);
	private final static String defaultIconPath = "/at/redeye/Framework/base/resources/icons/icon_small.png";

	private static class DefaultColumnSortComparator implements
			Comparator<DBValue> {

		@Override
		public int compare(DBValue o1, DBValue o2) {
			return (o1.getTitle().compareTo(o2.getTitle()));
		}

	}

	private List<DBValue> primKeyColumns = new ArrayList<DBValue>();
	private List<DBValue> dataColumns = new ArrayList<DBValue>();
	private List<DBValue> histColumns = new ArrayList<DBValue>();

	private PanelRegistry wReg = new PanelRegistry();

	private T dbStrukt;

	private GroupLayout layout;
	private GroupLayout.ParallelGroup horizGroup;
	private GroupLayout.SequentialGroup vertGroup;

	private String title;
	private String readButtonTxt = MlM("Lesen");
	private String saveButtonTxt = MlM("Speichern");
	private String closeButtonTxt = MlM("Schließen");
	private String primKeyPanelBorderTxt = MlM("Eindeutigkeit");

	// Swing controls -> dynamic
	private DBValueTypePanel dbField;

	public AbstractDbStruktDialog(Root root, String name, T dbStrukt) {
		super(root, name);
		this.dbStrukt = dbStrukt;
		this.title = MlM(dbStrukt.getTitle() + (" Wartungsmenü"));
		setRealPrimKeyFields();
		setDataFields();
		setHistFields();

	}

	@Override
	public void openDialog() {
		// Setup completed
		initComponents();
		setVisible(true);
	}

	@Override
	public void setPrimKeyExplanationFields(Collection<? extends DBValue> fields) {
		if (fields != null) {
			primKeyColumns.addAll(fields);
		}
	}

	@Override
	public void setCustomTitleText(String text) {
		this.title = MlM(text);
	}

	@Override
	public void setDateTimeGuiComponent(Class<? extends IDateTimeComponent> comp) {
		wReg.setDateTimeComponent(comp);
	}

	@Override
	public void setGuiComponent4DBValue(
			Entry<? extends DBValue, Class<? extends JComponent>> entry) {
		wReg.setGuiComponent4DBValue(entry);
	}

	private void setRealPrimKeyFields() {

		for (DBValue value : dbStrukt.getAllValues()) {
			if (value.isPrimaryKey()) {
				primKeyColumns.add(value);
			}
		}
		Collections.sort(primKeyColumns, new DefaultColumnSortComparator());

	}

	private void setDataFields() {

		for (DBValue value : dbStrukt.getAllValues()) {
			if (value.isPrimaryKey()) {
				continue;
			}
			dataColumns.add(value);
		}
		Collections.sort(dataColumns, new DefaultColumnSortComparator());
	}

	private void setHistFields() {

		Collection<DBStrukt> subs = dbStrukt.getSubStrukts();
		// search Hist
		for (DBStrukt s : subs) {
			if (s.getName().equalsIgnoreCase("hist")) {
				for (DBValue v : s.getAllValues()) {
					histColumns.add(v);
					dataColumns.remove(v);
				}
			}
		}
		Collections.sort(histColumns, new DefaultColumnSortComparator());
	}

	private void renderTitleGroup() {

		JLabel titleLabel = new JLabel(title);
		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(new ImageIcon(getClass()
				.getResource(defaultIconPath)));

		titleLabel.setFont(titleFont);
		horizGroup.addGroup(layout.createSequentialGroup()
				.addGap(50, 100, Short.MAX_VALUE).addComponent(titleLabel)
				.addGap(50, 100, Short.MAX_VALUE).addComponent(imageLabel));

		vertGroup.addGroup(layout.createParallelGroup(Alignment.CENTER, true)
				.addComponent(titleLabel).addComponent(imageLabel)
				.addGap(100, 100, 100));
	}

	private void renderPrimKeyPanel() {

		JPanel pkPanel = new JPanel();
		pkPanel.setBorder(BorderFactory
				.createTitledBorder(primKeyPanelBorderTxt));

		GroupLayout pkLayout = new GroupLayout(pkPanel);
		pkPanel.setLayout(pkLayout);
		ParallelGroup pkHorizGroup = pkLayout.createParallelGroup();
		pkLayout.setHorizontalGroup(pkHorizGroup);
		SequentialGroup pkVertGroup = pkLayout.createSequentialGroup();
		pkLayout.setVerticalGroup(pkVertGroup);

		// Primary key components (real and explanation)
		for (DBValue val : primKeyColumns) {
			dbField = new DBValueTypePanel(wReg, val);
			dbField.renderLayout();
			pkHorizGroup.addComponent(dbField, GroupLayout.DEFAULT_SIZE,
					GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			pkVertGroup.addComponent(dbField).addGap(10, 10, 10);

		}

		// Add primary key panel to main panel
		horizGroup.addComponent(pkPanel, GroupLayout.DEFAULT_SIZE,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		vertGroup.addComponent(pkPanel);
	}

	private void renderDataFields() {

		JTabbedPane tabPanel = new JTabbedPane();
		JPanel dataPanel = new JPanel();

		GroupLayout dataLayout = new GroupLayout(dataPanel);
		dataPanel.setLayout(dataLayout);
		ParallelGroup dataHorizGroup = dataLayout.createParallelGroup();
		dataLayout.setHorizontalGroup(dataHorizGroup);
		SequentialGroup dataVertGroup = dataLayout.createSequentialGroup();
		dataLayout.setVerticalGroup(dataVertGroup);

		tabPanel.addTab(MlM("Einstellungen"), dataPanel);
		// TODO: Support user tabs

		// Gap before the first field (up to down) is drawn inside data tab
		dataVertGroup.addGap(5);

		for (DBValue val : dataColumns) {
			dbField = new DBValueTypePanel(wReg, val);
			dbField.renderLayout();
			dataHorizGroup.addComponent(dbField, GroupLayout.DEFAULT_SIZE,
					GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			dataVertGroup.addGap(5).addComponent(dbField);
		}

		// History tab (should always be the last)
		if (!histColumns.isEmpty()) {
			JPanel histPanel = new JPanel();
			tabPanel.addTab(MlM("History"), histPanel);

			GroupLayout histLayout = new GroupLayout(histPanel);
			histPanel.setLayout(histLayout);
			ParallelGroup histHorizGroup = histLayout.createParallelGroup();
			histLayout.setHorizontalGroup(histHorizGroup);
			SequentialGroup histVertGroup = histLayout.createSequentialGroup();
			histLayout.setVerticalGroup(histVertGroup);

			// Gap before the first field (up to down) is drawn inside history tab
			histVertGroup.addGap(10);

			for (DBValue val : histColumns) {
				dbField = new DBValueTypePanel(wReg, val);
				dbField.renderLayout();
				histHorizGroup.addComponent(dbField, GroupLayout.DEFAULT_SIZE,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
				histVertGroup.addGap(5).addComponent(dbField);
			}
		}

		// Add data panel to main panel
		horizGroup.addComponent(tabPanel, GroupLayout.DEFAULT_SIZE,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		vertGroup.addComponent(tabPanel);
	}

	private void renderAndBindButtons() {

		JButton readButton = new JButton(readButtonTxt);
		JButton closeButton = new JButton(closeButtonTxt);
		JButton saveButton = new JButton(saveButtonTxt);

		horizGroup.addGroup(layout.createSequentialGroup().addGap(10)
				.addComponent(readButton).addGap(5, 10, 10)
				.addComponent(saveButton).addGap(5, 10, 10)
				.addComponent(closeButton));
		vertGroup
				.addGap(20)
				.addGroup(
						layout.createParallelGroup(Alignment.CENTER, true)
								.addComponent(readButton)
								.addComponent(saveButton)
								.addComponent(closeButton)).addGap(20);
		readButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				loadStuctureByPk();
			}
		});
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkAndSave();

			}
		});
	}

	protected void initComponents() {

		layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		horizGroup = layout.createParallelGroup();
		vertGroup = layout.createSequentialGroup();
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(horizGroup);
		layout.setVerticalGroup(vertGroup);

		// Title and icon
		renderTitleGroup();

		// Primary key section
		renderPrimKeyPanel();

		// Data fields
		renderDataFields();

		// Buttons
		renderAndBindButtons();

		pack();
	}

	private void loadStuctureByPk() {

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {

				for (DBValue pkVal : primKeyColumns) {
					if (!pkVal.isPrimaryKey()) {
						continue;
					}
					BasePanel panel = wReg.getPanelByName(pkVal.getName());
					panel.gui_to_var();

				}
				Transaction tx = getNewTransaction();
				if (tx == null) {
					logger.error("Keine Transaction bekommen!");
					return;
				}
				tx.fetchTableWithPrimkey(dbStrukt);
				tx.close();
				// update all data fields
				for (DBValue val : dataColumns) {
					BasePanel panel = wReg.getPanelByName(val.getName());
					panel.var_to_gui();
				}
				for (DBValue val : histColumns) {
					BasePanel panel = wReg.getPanelByName(val.getName());
					panel.var_to_gui();
				}
			}
		};
	}

	private void checkAndSave() {

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {

				// TODO: Check PK change

				Transaction tx = getNewTransaction();
				if (tx == null) {
					logger.error("Keine Transaction bekommen!");
					return;
				}
				for (DBValue val : histColumns) {
					if (val.getName().equals("aeuser")) {
						val.loadFromString(root.getUserName().isEmpty() ? "System"
								: root.getUserName());
					} else if (val.getName().equals("aezeit")) {
						val.loadFromCopy(new Date(System.currentTimeMillis()));
					}
				}
				for (DBValue val : dataColumns) {
					BasePanel panel = wReg.getPanelByName(val.getName());
					panel.gui_to_var();
				}
				tx.updateValues(dbStrukt);
				tx.commit();
				tx.close();
				loadStuctureByPk();
			}
		};
	}
}
