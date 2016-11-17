package com.sanjoyghosh.company.swing;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.sanjoyghosh.company.api.ITableItem;

public class EarningsTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 5900811220781168284L;
	
	
	private String[] 			columnNames;
	private List<ITableItem> 	items;
	

	public EarningsTableModel(String[] columnNames, List<ITableItem> items) {
		this.columnNames = columnNames;
		this.items = items;
	}
	

	public int getRowCount() {
		return items == null ? 0 : items.size();
	}

	
	public int getColumnCount() {
		return columnNames == null ? 0 : columnNames.length;
	}
	

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (items == null || items.size() <= rowIndex) {
			return null;
		}
		return items.get(rowIndex).getData(columnIndex);
	}


	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
}
