package com.sanjoyghosh.company.earnings;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JPanel;

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


	public UpcomingEarningsPanel() {
		headerPanel = new JPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(headerPanel);

		startDate = new GregorianCalendar();
		startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
		numberOfDays = 7;

		List<ITableItem> earningsList = CompanyApi.getCompanyEarnings(startDate, numberOfDays);
		earningsTable = new EarningsTable(CompanyEarnings.getColumnNames(), earningsList, new Dimension(500, 500));
		add(earningsTable.getScrollPane());
	}
}
