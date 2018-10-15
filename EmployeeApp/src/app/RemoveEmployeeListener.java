package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTable;

public class RemoveEmployeeListener extends JFrame implements ActionListener {
	
	JTable table;
	MyTableModel tm;
	
	public RemoveEmployeeListener(JTable myTable, MyTableModel model) {

		this.table = myTable;
		this.tm = model;
		
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		tm.deleteRow(table.getSelectedRow());
		
		
	}

	
}
