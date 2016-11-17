package com.sanjoyghosh.company.swing;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.sanjoyghosh.company.api.ITableItem;

public class EarningsTable {

	private EarningsTableModel	tableModel;
	private JTable				table;
	private JScrollPane			scrollPane;
	
	
	public EarningsTable(String[] tableColumnNames, List<ITableItem> tableItems) {
		tableModel = new EarningsTableModel(tableColumnNames, tableItems);
		
		table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		
		scrollPane = new JScrollPane(table);
	}


	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}
