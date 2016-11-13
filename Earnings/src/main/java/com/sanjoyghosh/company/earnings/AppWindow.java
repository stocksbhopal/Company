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
		
		JPanel contentPanel = new JPanel();
		frame.getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(1, 1, 0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPanel.add(tabbedPane);
		
		JPanel upcomingEarningsPanel = new JPanel();
		tabbedPane.addTab("Upcoming Earnings", null, upcomingEarningsPanel, null);

		Object[][] data = AppAdapter.getAllCompanyEarnings();
		Object[] columns = { "Symbol", "Name", "Earnings Date"};
		upcomingEarningsTable = new JTable(data, columns);
		upcomingEarningsTable.setPreferredSize(new Dimension(1000, 500));

		JScrollPane upcomingEarningsScrollPane = new JScrollPane(upcomingEarningsTable);
		upcomingEarningsPanel.add(upcomingEarningsScrollPane);
		
		
//		upcomingEarningsTable = new JTable();
		
//		upcomingEarningsPanel.setPreferredSize(new Dimension(800, 600));
//		upcomingEarningsScrollPane.setPreferredSize(new Dimension(800, 600));
//		upcomingEarningsTable.setPreferredSize(new Dimension(800,  600));
				
		upcomingEarningsTable.setPreferredScrollableViewportSize(upcomingEarningsTable.getPreferredSize());
		upcomingEarningsTable.setFillsViewportHeight(true);

		System.out.println(upcomingEarningsTable.getPreferredSize());
		System.out.println(upcomingEarningsTable.getPreferredScrollableViewportSize());
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_2, null);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_3, null);
		
		frame.pack();
	}
}
