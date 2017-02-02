package com.sanjoyghosh.company.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import javax.swing.DefaultComboBoxModel;

public class StockList {

	private JFrame frmStockList;
	private JTextField nameTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StockList window = new StockList();
					window.frmStockList.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StockList() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmStockList = new JFrame();
		frmStockList.setTitle("Sanjoy's Earnings Helper Stock List");
		frmStockList.setBounds(100, 100, 810, 643);
		frmStockList.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStockList.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel headerPanel = new JPanel();
		frmStockList.getContentPane().add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		UtilDateModel model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);
		datePicker.setToolTipText("Date list was initiated");
		 
		headerPanel.add(datePicker);
		
		JComboBox<String> sourceComboBox = new JComboBox<>();
		sourceComboBox.setModel(new DefaultComboBoxModel(new String[] {"Merrill Lynch", "Morgan Stanley", "J P Morgan", ""}));
		sourceComboBox.setToolTipText("List Sources");
		headerPanel.add(sourceComboBox);
		
		nameTextField = new JTextField();
		nameTextField.setToolTipText("Name of the List");
		nameTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		headerPanel.add(nameTextField);
		nameTextField.setColumns(30);
		
		JScrollPane listScrollPane = new JScrollPane();
		frmStockList.getContentPane().add(listScrollPane, BorderLayout.CENTER);
		
		JPanel footerPanel = new JPanel();
		frmStockList.getContentPane().add(footerPanel, BorderLayout.SOUTH);
		
		JButton deleteButton = new JButton("Delete");
		footerPanel.add(deleteButton);
		
		JButton addButton = new JButton("Add Stock");
		footerPanel.add(addButton);
		
		JButton saveButton = new JButton("Save");
		footerPanel.add(saveButton);
		
		JButton cancelButton = new JButton("Cancel");
		footerPanel.add(cancelButton);
	}
}
