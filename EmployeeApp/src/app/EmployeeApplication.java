package app;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DateFormatter;

import java.text.DateFormat;

public class EmployeeApplication extends JFrame {
	JTable myTable;

	// Menu structure
	JMenuBar myBar;
	JMenu fileMenu, employeeMenu, exitMenu;
	JMenuItem fileLoad, fileSave, fileSaveAs, filePrintReport, addEmployee, removeEmployee, exitProgram;

	// Array of data types to be used in combo box when defining new structure
	String[] dataTypeNames;

	JPanel p;
	MyTableModel tm;
	JScrollPane myPane;

	// Subdialog used when defining new structure
	// DepositFundsDialog depositDialog;
	JLabel lblPPSNo, lblName, lblGender, lblDepartment, lblPosition, lblSalary;
	JTextField txtPPSNo, txtName, txtDepartment, txtPosition, txtSalary;
	JComboBox cbGender, comboBox;
	String[] genderArr = { "Male", "Female" };
	JButton btnNew, btnUpdate, btnDelete, btnPrintAll, btnClose;
	Vector<Object> newDetails;

	// Used to indicate whether data is already in a file
	File currentFile;

	// Input Stream

	ObjectInputStream in;
	FileInputStream fin;

	// Listeners
	AddEmployeeListener addListener = new AddEmployeeListener();

	public EmployeeApplication() {

		// Create vector to add to AddEmployeeListener class to pass details into the
		// MyTableModelClass
		newDetails = new Vector<Object>();

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create menu bar and table to panel
		JMenuBar myBar = createMenuBar();
		this.setJMenuBar(myBar);

		p = new JPanel();
		tm = new MyTableModel();
		myTable = new JTable(tm);
		myPane = new JScrollPane(myTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		myTable.setSelectionForeground(Color.white);
		myTable.setSelectionBackground(Color.red);
		myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		p.setLayout(new BorderLayout());
		myPane.setPreferredSize(new Dimension(450, 110));
		p.add(myPane, BorderLayout.NORTH);

		JPanel myCenterPanel = createCentrePanel();
		Border line = BorderFactory.createLineBorder(Color.black);
		myCenterPanel.setBorder(BorderFactory.createTitledBorder(line, "Employee Records Editor"));

		p.add(myCenterPanel, BorderLayout.CENTER);

		JPanel myBottomPanel = createBottomPanel();
		p.add(myBottomPanel, BorderLayout.SOUTH);

		// Associating event listeners with menu items
		// TODO: on row selection populate all the textfields and combobox with the
		// values from the selected row
		
		myTable.addMouseListener(new MouseAdapter() {
				public void mouseClicked (MouseEvent clkt) {
			
					// get current row
					int row = myTable.getSelectedRow();
					
					// update all values from txt fields to table
					txtPPSNo.setText(tm.getValueAt(row, 0).toString());
					txtName.setText(tm.getValueAt(row, 1).toString());
					cbGender.setSelectedItem(tm.getValueAt(row, 2));
					txtDepartment.setText(tm.getValueAt(row, 3).toString());
					txtPosition.setText(tm.getValueAt(row, 4).toString());
					txtSalary.setText(tm.getValueAt(row, 5).toString());
			
				}
		});

		// TODO: Add Employee record - both the add button and the add employee menu
		// item should use
		// the same event handler/listener to add a record and insert a row

		btnNew.addActionListener(addListener);
		addEmployee.addActionListener(addListener);

		// TODO: Remove Employee record - both the delete button and the remove employee
		// menu item should use
		// the same event handler/listener to delete a record
		
		

		btnDelete.addActionListener(new RemoveEmployeeListener(myTable, tm));
		removeEmployee.addActionListener(new RemoveEmployeeListener(myTable, tm));
		
		//** another way to do the same thing **\\
		/*
		 * 	RemoveEmployeeListener remove = new RemoveEmployeeListener(myTable, tm);
			btnDelete.addActionListener(remove);
			removeEmployee.addActionListener(remove);
		*/

		// TODO:Update button
		// anonymous inner class
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
				// get current row being edited
				int row = myTable.getSelectedRow();
				
				
				
				// set all values in the table equal to what is currently in the text boxes (whether edited or not) 
				tm.setValueAt(txtPPSNo.getText(), row, 0);
				tm.setValueAt(txtName.getText(), row, 1);
				tm.setValueAt(cbGender.getSelectedItem().toString(), row, 2);
				tm.setValueAt(txtDepartment.getText(), row, 3);
				tm.setValueAt(txtPosition.getText(), row, 4);
				tm.setValueAt(Double.parseDouble(txtSalary.getText()), row, 5);
				
				// Ensure there is a row to update, otherwise let user know there is nothing in the table yet
				} catch (IndexOutOfBoundsException expt) {
					
					JOptionPane.showMessageDialog(null, "There is no data t update! Please add a row.");
					
				}
				
			}
		});
		

		// TODO load the contents of the employee.dat file into the table
		fileLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Open a file chooser to allow user to select a file
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(EmployeeApplication.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					// ask user if they are sure they want to overwrite current data
					int confirm = JOptionPane.showConfirmDialog(null, "This will replace the existing data. Are you sure you want to continue?");
					
			if(confirm == JOptionPane.YES_OPTION) {
					
				File file = fc.getSelectedFile();

					try {

						readDataFile(file);

					} catch (IOException io) {

						JOptionPane.showMessageDialog(EmployeeApplication.this, "I/O Exception\n " + io.toString(),
								"Error Message", JOptionPane.ERROR_MESSAGE);

					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} // end try-catch
				} // end if
				else {
					
					
				} // end else

			}
			}
		});

		// Saving the file - usually in a different location
		fileSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(EmployeeApplication.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// Check to see if there is already a file with this name
					if (file.exists()) {
						int result = JOptionPane.showConfirmDialog(EmployeeApplication.this,
								"This will overwrite the existing file.\n Are you sure you want to do this?");
						if (result == 0) {
							try {
								// We put the implementation of writing into a separate method
								writeDataFile(file);
							} catch (IOException ex) {
								JOptionPane.showMessageDialog(EmployeeApplication.this,
										"I/O Exception\n " + ex.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					// This is a new file name
					else {
						try {
							writeDataFile(file);
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(EmployeeApplication.this, "I/O Exception\n " + ex.toString(),
									"Error Message", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		// Saving the file - usually in the same location
		fileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Data may not yet have been saved to a file
				// currentFile not being null means that the file object has been created and we
				// then check that it exists in the file system
				if (currentFile != null && currentFile.exists()) {
					int result = JOptionPane.showConfirmDialog(EmployeeApplication.this,
							"This will overwrite the existing file.\n Are you sure you want to do this?");
					if (result == 0) {
						try {
							writeDataFile(currentFile);
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(EmployeeApplication.this, "I/O Exception\n " + ex.toString(),
									"Error Message", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				// Otherwise we need to specify the name to use for this file
				else {
					JOptionPane.showMessageDialog(EmployeeApplication.this, "File doesn't exist.\n", "Error message",
							JOptionPane.ERROR_MESSAGE);
					// Ask user to specify file name (remember user can type in new file name in
					// file chooser)
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showSaveDialog(EmployeeApplication.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						currentFile = fc.getSelectedFile();
						if (currentFile.exists()) {
							int result = JOptionPane.showConfirmDialog(EmployeeApplication.this,
									"This will overwrite the existing file.\n Are you sure you want to do this?");
							if (result == 0) {
								try {
									writeDataFile(currentFile);
								} catch (IOException ex) {
									JOptionPane.showMessageDialog(EmployeeApplication.this,
											"I/O Exception\n " + ex.toString(), "Error Message",
											JOptionPane.ERROR_MESSAGE);
								}
							}
						} else {
							try {
								writeDataFile(currentFile);
							} catch (IOException ex) {
								JOptionPane.showMessageDialog(EmployeeApplication.this,
										"I/O Exception\n " + ex.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});

		// Prints report as external HTML file with name BankReport.html

		filePrintReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				BufferedWriter out = null;
				
				/* GET THE DATE*/
				
				// get todays date
				Date today = Calendar.getInstance().getTime();
				// create a date formatter
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				// create a new string of this format
				String theDate = df.format(today);
				
				try {
					
					out = new BufferedWriter(new FileWriter("EmployeeReport.html"));
					
					// Write column headers as one object
					out.write("<html>");
					out.write("<body>");
					out.write("<p>Report at: " + theDate + "</p>");
					out.write("<br />");
					out.write("<table border = \"1\">");
					out.write("<tr><th>PPS Number:</th><th>Name</th><th>Gender</th><th>Department</th><th>Position</th><th>Salary</th></tr>");
					
					
				for(int i = 0; i < tm.getRowCount(); i++) {
					
					// write the table data as a line array and add it line by line
					String [] line = {"<tr><td>" + tm.getValueAt(i, 0).toString() + "</td>", "<td>" + tm.getValueAt(i, 1).toString() + "</td>", "<td>" + tm.getValueAt(i, 2).toString() + "</td>", "<td>" + tm.getValueAt(i, 3).toString() + "</td>", "<td>" + tm.getValueAt(i, 4).toString() + "</td>", "<td>" + tm.getValueAt(i, 5).toString() + "</td></tr>"};
					
					for(int j = 0; j <line.length; j++) {
						
						out.write(line[j]);
					}
					
				
				out.write("</table>");
				out.write("</body>");
				out.write( "</html>");
				
				} // end for
							
			
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(EmployeeApplication.this, "I/O Exception\n " + ex.toString(), "Error Message",
							JOptionPane.ERROR_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(EmployeeApplication.this, "Exception\n " + ex.toString(), "Error Message",
							JOptionPane.ERROR_MESSAGE);
				}
				// Make sure to close stream
				
				finally {
					
					try {
						out.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});

		// Both of these listeners behave in the same way so we extract their code to a
		// separate method

		// exits program from menu
		exitProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDown();
			}
		});

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDown();
			}
		});

		// exits program by closing window
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeDown();
			}
		}); // end windowlistener

		this.setTitle("Employee Application");
		this.setContentPane(p);
		this.setVisible(true);
		this.pack();
	} // constructor

	public void readDataFile(File f) throws IOException, ClassNotFoundException {
		// Initialize needed variables
		ObjectInputStream in = null;
		Vector<Object> data = null;
		Vector<String> cols = null;
		Vector<Object> fileData = null;

		// try read from file
		try {
			// create input stream
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));

			fileData = new Vector<Object>();
			
			// while there is data to read
			while (true) {
	
				fileData.add(in.readObject());
				
			} // end while
			// so the user doesn't get an error message
		} catch (EOFException eof) {
			

		} finally {
			
				in.close();
		}

		cols = (Vector<String>) fileData.elementAt(0);
		data = (Vector<Object>) fileData.elementAt(1);
		
		// set the data in the table
		tm.setColumnNames(cols);
		tm.setTableData(data);

	}

	public void writeDataFile(File f) throws IOException, FileNotFoundException {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
			// Write column headers as one object
			out.writeObject(tm.getColumnNames());
			// Write table data as second object
			out.writeObject(tm.getTableData());
			// This indicates that there is no unsaved data for the moment
			tm.modified = false;
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(EmployeeApplication.this, "File Not Found Exception\n " + ex.toString(),
					"Error Message", JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(EmployeeApplication.this, "I/O Exception\n " + ex.toString(), "Error Message",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(EmployeeApplication.this, "Exception\n " + ex.toString(), "Error Message",
					JOptionPane.ERROR_MESSAGE);
		}
		// Make sure to close stream
		finally {
			out.close();
		}
	}

	// This behaviour will be used whether we close the application by clicking on
	// the X button in the top-right corner or by selecting
	// an option from a menu, so it makes sense to have it as an independent method
	// that can be reused.
	public void closeDown() {
		// Ask user to confirm decision
		int result = JOptionPane.showConfirmDialog(EmployeeApplication.this,
				"This will close the application.\n Are you sure you want to do this?");
		if (result == 0) {
			// Give user second chance if there is unsaved data
			if (tm.modified) {
				int result2 = JOptionPane.showConfirmDialog(EmployeeApplication.this,
						"You have unsaved data that will be lost.\n Are you sure you want to do this?");
				if (result2 == 0) {
					System.exit(0);
				}
			} else {
				System.exit(0);
			}
		}
	}

	public JMenuBar createMenuBar() {
		// Setting up menu
		fileLoad = new JMenuItem("Open");
		fileSave = new JMenuItem("Save");
		fileSaveAs = new JMenuItem("Save As");
		filePrintReport = new JMenuItem("Print Report");

		fileMenu = new JMenu("File");
		fileMenu.add(fileLoad);
		fileMenu.add(fileSave);
		fileMenu.add(fileSaveAs);
		fileMenu.add(filePrintReport);

		addEmployee = new JMenuItem("Add");
		removeEmployee = new JMenuItem("Remove");

		employeeMenu = new JMenu("Employees");
		employeeMenu.add(addEmployee);
		employeeMenu.add(removeEmployee);

		exitProgram = new JMenuItem("Exit Program");
		exitMenu = new JMenu("Exit");
		exitMenu.add(exitProgram);

		myBar = new JMenuBar();
		myBar.add(fileMenu);
		myBar.add(employeeMenu);
		myBar.add(exitMenu);

		return myBar;

	}

	public JPanel createCentrePanel() {
		JPanel centerPanel = new JPanel(new GridLayout(6, 2));

		lblPPSNo = new JLabel("PPS Number", JLabel.LEFT);
		lblName = new JLabel("FullName", JLabel.LEFT);
		lblGender = new JLabel("Gender", JLabel.LEFT);
		lblDepartment = new JLabel("Department", JLabel.LEFT);
		lblPosition = new JLabel("Position", JLabel.LEFT);
		lblSalary = new JLabel("Salary", JLabel.LEFT);

		txtPPSNo = new JTextField(20);
		txtName = new JTextField(20);
		txtDepartment = new JTextField(20);
		txtPosition = new JTextField(20);
		txtSalary = new JTextField(20);
		txtSalary.setText("0.0");
		String[] genderArr = { "Male", "Female" };
		cbGender = new JComboBox(genderArr);

		centerPanel.add(lblPPSNo);
		centerPanel.add(txtPPSNo);
		centerPanel.add(lblName);
		centerPanel.add(txtName);
		centerPanel.add(lblGender);
		centerPanel.add(cbGender);
		centerPanel.add(lblDepartment);
		centerPanel.add(txtDepartment);
		centerPanel.add(lblPosition);
		centerPanel.add(txtPosition);
		centerPanel.add(lblSalary);
		centerPanel.add(txtSalary);

		return centerPanel;

	}

	public JPanel createBottomPanel() {
		JPanel BottomPanel = new JPanel();
		btnNew = new JButton("Add New");
		btnUpdate = new JButton("Update");
		btnDelete = new JButton("Delete");
		;
		btnPrintAll = new JButton("Print All");
		btnClose = new JButton("Close");
		BottomPanel.add(btnNew);
		BottomPanel.add(btnUpdate);
		BottomPanel.add(btnDelete);
		BottomPanel.add(btnPrintAll);
		BottomPanel.add(btnClose);

		return BottomPanel;

	}

	public static void main(String args[]) throws IOException {
		new EmployeeApplication();
	} // main

	// Inner Listener Class to add Employee from text boxes
	public class AddEmployeeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			int theNum = 0;
			NumberFormat formatter = new DecimalFormat("#0,000.00");
			
			// 1. Check to make sure all textfields have a value

			if (txtPPSNo.getText().equals("") || txtName.getText().equals("") || txtDepartment.getText().equals("")
					|| txtPosition.getText().equals("") || txtSalary.getText().equals("")) {

				JOptionPane.showMessageDialog(null, "Error! You must fill all fields!");

			} // end if text fields must have a value

			// 2. Ensure PPSN entered is 8 chars long and contains 1 letter
			else if (txtPPSNo.getText().toString()
					.length() != 8 || !txtPPSNo.getText().toString().matches("[0-9]{7}[A-Z]{1}")) {

				JOptionPane.showMessageDialog(null, "Please enter and 8/9 digit PPSN in the format 1234567A/ 12345678A");
				txtPPSNo.setText("");

			} // end else if PPSNo

			else {
				
				// check for duplicates
				for(int i = 0; i < tm.getRowCount(); i++) {
					
						if(tm.getValueAt(i, 0).toString().equals(txtPPSNo.getText())) {
							
							JOptionPane.showMessageDialog(null, "An employee with this record already exists!");
							txtPPSNo.setText("");
							txtName.setText("");
							txtDepartment.setText("");
							txtPosition.setText("");
							txtSalary.setText("");
						}
					
				}

				try {
					
					// validate input for salary to be a real number
					formatter.format(Double.parseDouble(txtSalary.getText()));
					newDetails.add(5, Double.parseDouble(txtSalary.getText()));
					
					Vector<Object> newDetails = new Vector<Object>();
					
					// if everything is correct create a vector with details to be passed in
					newDetails.add(0, txtPPSNo.getText());
					newDetails.add(1, txtName.getText());
					newDetails.add(2, cbGender.getSelectedItem().toString());
					newDetails.add(3, txtDepartment.getText());
					newDetails.add(4, txtPosition.getText());
					
					tm.addRow(newDetails);

					txtPPSNo.setText("");
					txtName.setText("");
					txtDepartment.setText("");
					txtPosition.setText("");
					txtSalary.setText("");
					
					// 3. Ensure Salary is a double
				} catch (NumberFormatException e) {

					JOptionPane.showMessageDialog(null, "Please input a number value for Salary!");
					txtSalary.setText("");
				}

			} // end action listener method

		}
	}
}
