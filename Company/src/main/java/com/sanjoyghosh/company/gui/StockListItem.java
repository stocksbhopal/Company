package com.sanjoyghosh.company.gui;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import javax.swing.JTextField;

public class StockListItem extends JPanel {

	private static final long serialVersionUID = 4376072974356807165L;

	
	private JTextField symbolTextField;
	private JTextField nameTextField;

	/**
	 * Create the panel.
	 */
	public StockListItem() {
		
		JCheckBox selectCheckBox = new JCheckBox("");
		add(selectCheckBox);
		
		UtilDateModel model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);
		 
		add(datePicker);
		
		symbolTextField = new JTextField();
		add(symbolTextField);
		symbolTextField.setColumns(10);
		
		nameTextField = new JTextField();
		add(nameTextField);
		nameTextField.setColumns(10);
	}
}
