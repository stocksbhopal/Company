package com.sanjoyghosh.company.earnings;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JSplitPane;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTable;

import com.sanjoyghosh.company.api.CompanyApi;

public class AppWindow {

	private JFrame frame;
	private JTable upcomingEarningsTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		CompanyApi.init();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = new AppWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(0, 0, 1200, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		Object[][] data = AppAdapter.getAllCompanyEarnings();
		Object[] columns = { "Symbol", "Name", "Earnings Date"};
		upcomingEarningsTable = new JTable(data, columns);

		JScrollPane upcomingEarningsScrollPane = new JScrollPane(upcomingEarningsTable);

		tabbedPane.addTab("Upcoming Earnings", null, upcomingEarningsScrollPane, null);
		upcomingEarningsTable.setFillsViewportHeight(true);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_2, null);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_3, null);
		
		frame.getContentPane().add(tabbedPane);
		frame.pack();
	}
}
