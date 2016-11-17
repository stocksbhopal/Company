package com.sanjoyghosh.company.swing;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.sanjoyghosh.company.api.ITableItem;

public class EarningsTable {

	private EarningsTableModel	tableModel;
	private JTable				table;
	private JScrollPane			scrollPane;
	
	
	public EarningsTable(String[] tableColumnNames, List<ITableItem> tableItems, Dimension tablePreferredSize) {
		tableModel = new EarningsTableModel(tableColumnNames, tableItems);
		
		table = new JTable(tableModel);
		table.setPreferredSize(tablePreferredSize);
		table.setFillsViewportHeight(true);
		
		scrollPane = new JScrollPane(table);
	}


	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}
