package com.sanjoyghosh.company.earnings;

import java.awt.GridLayout;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.sanjoyghosh.company.api.CompanyApi;
import com.sanjoyghosh.company.api.CompanyEarnings;
import com.sanjoyghosh.company.api.ITableItem;
import com.sanjoyghosh.company.swing.EarningsTable;

public class UpcomingEarningsPanel extends JPanel {

	private static final long serialVersionUID = -2178010785215120065L;
	
	private GregorianCalendar	startDate;
	private int					numberOfDays;
	private JPanel				headerPanel;
	private EarningsTable		earningsTable;
	private JTextField numberOfDaysTextField;
	private JTextField startDateTextField;


	public UpcomingEarningsPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		headerPanel = new JPanel();
		add(headerPanel);
		headerPanel.setLayout(new GridLayout(2, 4));

		JLabel startDateLabel = new JLabel("Start Date");
		headerPanel.add(startDateLabel);

		JLabel numberOfDaysLabel = new JLabel("Num Days");
		headerPanel.add(numberOfDaysLabel);

		JLabel blank1Label = new JLabel("");
		headerPanel.add(blank1Label);

		JLabel blank2Label = new JLabel("");
		headerPanel.add(blank2Label);

		startDateTextField = new JTextField();
		headerPanel.add(startDateTextField);
		startDateTextField.setColumns(10);
		
		numberOfDaysTextField = new JTextField();
		headerPanel.add(numberOfDaysTextField);
		numberOfDaysTextField.setColumns(10);

		JLabel blank3Label = new JLabel("");
		headerPanel.add(blank3Label);

		JButton goButton = new JButton("Go");
		headerPanel.add(goButton);

		headerPanel.setMinimumSize(headerPanel.getPreferredSize());
		headerPanel.setMaximumSize(headerPanel.getPreferredSize());

		startDate = new GregorianCalendar();
		startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
		numberOfDays = 7;

		List<ITableItem> earningsList = CompanyApi.getCompanyEarnings(startDate, numberOfDays);
		earningsTable = new EarningsTable(CompanyEarnings.getColumnNames(), earningsList);
		add(earningsTable.getScrollPane());
	}
}
