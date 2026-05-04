package rna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class Main{
	static JFrame f = new JFrame();
	
	static DefaultTableModel model = new DefaultTableModel(); 
	static DefaultTableModel tempModel = new DefaultTableModel();
	static JTable table = new JTable(tempModel); 
	static JScrollPane sp=new JScrollPane(table);   
	
	static JButton searchButton = new JButton("Search");
	static JTextField searchTextField = new JTextField();
	static JButton clearButton = new JButton("Clear");
	static JLabel numberOfResultsLabel = new JLabel();
	static JLabel averageSequenceLengthLabel = new JLabel();
	
	static JButton saveTableButton = new JButton("Export Table");
//	static JButton saveSelectedButton = new JButton("Save Selected");
	
	static JButton appendDBButton = new JButton("Append DB");
	static JButton clearDBButton = new JButton("Clear DB");
//	static JLabel loadingLabel = new JLabel("Loading");
	
	static FileDialog loadFileDialog = new FileDialog(f, "Choose a file", FileDialog.LOAD);
	static FileDialog saveFileDialog = new FileDialog(f, "Save file", FileDialog.SAVE);

	static BufferedReader objReader = null;
	static Connection c = null;
	static Statement stmt = null;
	static String sql = "";
	
	static int pageNumber = 0;
	static JButton nextPage = new JButton("->");
	static JButton previousPage = new JButton("<-");
	static JButton goToPage = new JButton("Go");
	static JTextField pageTextField = new JTextField();
	
	private static void setupGUI() {
		f.setSize(1300,700);
		f.addWindowListener((WindowListener) new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectDB();
                System.exit(0);
            }
        });
		
		
		model.addColumn("ID"); 
		model.addColumn("proteins"); 
		model.addColumn("accessions"); 
		model.addColumn("sequences"); 
		model.addColumn("annotions"); 
		model.addColumn("interpros"); 
		model.addColumn("orgs"); 
		
		tempModel.addColumn("ID"); 
		tempModel.addColumn("proteins"); 
		tempModel.addColumn("accessions"); 
		tempModel.addColumn("sequences"); 
		tempModel.addColumn("annotions"); 
		tempModel.addColumn("interpros"); 
		tempModel.addColumn("orgs"); 
		
		 
		sp.setBounds(30,70,1220,520);     
		sp.setVisible(true);
		f.add(sp);  

		
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search(searchTextField.getText());
				pageNumber=0;
				loadGUIFromTableModel(pageNumber);
	        }
	    });
		searchButton.setBounds(330, 40,80,20);
		searchButton.setVisible(true);
		f.add(searchButton);
		
		searchTextField.setBounds(30, 40,300,20);
		searchTextField.setVisible(true);
		f.add(searchTextField);
		
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadTableModelFromDB();
				pageNumber=0;
				loadGUIFromTableModel(pageNumber);
				numberOfResultsLabel.setVisible(false);
				averageSequenceLengthLabel.setVisible(false);
				searchTextField.setText("");
	        }
	    });
		clearButton.setBounds(415, 40,80,20);
		clearButton.setVisible(true);
		f.add(clearButton);
		
		appendDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rows = model.getRowCount();
				for(int i = 0;i < rows;i++) {
					model.removeRow(0);
				}
				loadFileDialog.setMultipleMode(true);
				loadFileDialog.setDirectory("C:\\");
				loadFileDialog.setVisible(true);
				File files[] = loadFileDialog.getFiles();
				f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		        for (File file : files) {
		          String filename = file.getAbsolutePath();
		          if(filename != null) appendDBFromFile(filename);		
		        }
				loadTableModelFromDB();
				pageNumber=0;
				loadGUIFromTableModel(pageNumber);
				f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				numberOfResultsLabel.setVisible(false);
				averageSequenceLengthLabel.setVisible(false);
				searchTextField.setText("");
	        }
	    });
		appendDBButton.setBounds(30, 10,80,20);
		appendDBButton.setVisible(true);
		f.add(appendDBButton);
		
		clearDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearTable();
				pageNumber=0;
				loadGUIFromTableModel(pageNumber);
				numberOfResultsLabel.setVisible(false);
				averageSequenceLengthLabel.setVisible(false);
				searchTextField.setText("");
	        }
	    });
		clearDBButton.setBounds(115, 10,80,20);
		clearDBButton.setVisible(true);
		f.add(clearDBButton);
		
		saveTableButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFileDialog.setDirectory("C:\\");
				saveFileDialog.setVisible(true);
				String filename = saveFileDialog.getDirectory() + saveFileDialog.getFile();
				if(filename.endsWith(".csv"))exportToCSV(tempModel,filename);
				else if(filename.endsWith(".tsv"))exportToTSV(tempModel,filename);
				else JOptionPane.showMessageDialog(f, "Wrong extension! Please use .csv or .tsv formats");
	        }
	    });
		saveTableButton.setBounds(1150, 10,100,20);
		saveTableButton.setVisible(true);
		f.add(saveTableButton);
		
		numberOfResultsLabel.setBounds(30, 600,300,20);
		numberOfResultsLabel.setVisible(false);
		f.add(numberOfResultsLabel);
		
		averageSequenceLengthLabel.setBounds(30, 630,300,20);
		averageSequenceLengthLabel.setVisible(false);
		f.add(averageSequenceLengthLabel);
		
//		saveSelectedButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//	        }
//	    });
//		saveSelectedButton.setBounds(1270, 40,100,20);
//		saveSelectedButton.setVisible(true);
//		f.add(saveSelectedButton);
		
//		loadingLabel.setBounds(660,380,80,20);
		
		nextPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pageNumber++;
				if(pageNumber > model.getRowCount()/31) pageNumber = model.getRowCount()/31;
				loadGUIFromTableModel(pageNumber);
	        }
	    });
		nextPage.setBounds(1170, 600,80,20);
		nextPage.setVisible(true);
		f.add(nextPage);
		
		previousPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pageNumber--;
				if(pageNumber <0)pageNumber = 0;
				loadGUIFromTableModel(pageNumber);
	        }
	    });
		previousPage.setBounds(1080, 600,80,20);
		previousPage.setVisible(true);
		f.add(previousPage);
		
		pageTextField.setBounds(1080, 630,80,20);
		pageTextField.setVisible(true);
		f.add(pageTextField);
		
		goToPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pageNumber = Integer.valueOf(pageTextField.getText()) - 1;
				if(pageNumber > model.getRowCount()/31) pageNumber = model.getRowCount()/31;
				if(pageNumber <0)pageNumber = 0;
				loadGUIFromTableModel(pageNumber);
	        }
	    });
		goToPage.setBounds(1170, 630,80,20);
		goToPage.setVisible(true);
		f.add(goToPage);

		f.setLayout(null);
		f.setVisible(true);
	}
	
	
	private static void exportToCSV(TableModel modelToExport, String pathToExportTo) {
	    try {

	        FileWriter csv = new FileWriter(new File(pathToExportTo));

	        for (int i = 0; i < modelToExport.getColumnCount(); i++) {
	        	if(i < modelToExport.getColumnCount() - 1) csv.write(modelToExport.getColumnName(i) + ",");
	        	else csv.write(modelToExport.getColumnName(i));
	        }

	        csv.write("\n");

	        for (int i = 0; i < modelToExport.getRowCount(); i++) {
	            for (int j = 0; j < modelToExport.getColumnCount(); j++) {
	            	if(j == 4) {
	            		csv.write("['" + modelToExport.getValueAt(i, j).toString().replace(" GO:", "', 'GO:") + "'],");
	            	}
	            	else {
		            	if(j < modelToExport.getColumnCount() - 1)csv.write(modelToExport.getValueAt(i, j).toString() + ",");
		            	else csv.write(modelToExport.getValueAt(i, j).toString());
	            	}
	            }
	            csv.write("\n");
	        }

	        csv.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private static void exportToTSV(TableModel modelToExport, String pathToExportTo) {
	    try {

	        FileWriter csv = new FileWriter(new File(pathToExportTo));

	        for (int i = 0; i < modelToExport.getColumnCount(); i++) {
	        	if(i < modelToExport.getColumnCount() - 1) csv.write(modelToExport.getColumnName(i) + "\t");
	        	else csv.write(modelToExport.getColumnName(i));
	        }

	        csv.write("\n");

	        for (int i = 0; i < modelToExport.getRowCount(); i++) {
	            for (int j = 0; j < modelToExport.getColumnCount(); j++) {
	            	if(j == 4) {
	            		csv.write("['" + modelToExport.getValueAt(i, j).toString().replace(" GO:", "', 'GO:") + "']\t");
	            	}
	            	else {
		            	if(j < modelToExport.getColumnCount() - 1)csv.write(modelToExport.getValueAt(i, j).toString() + "\t");
		            	else csv.write(modelToExport.getValueAt(i, j).toString());
	            	}
	            }
	            csv.write("\n");
	        }

	        csv.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	private static void search(String search) {
		try {
			String sql = "SELECT ID,proteins,accessions,sequences,annotations,interpros,orgs "
	                + "FROM RNA WHERE proteins LIKE '%" + search + "%'"
	                + " OR accessions LIKE '%" + search + "%'"
	                + " OR sequences LIKE '%" + search + "%'"
	                + " OR annotations LIKE '%" + search + "%'"
	                + " OR interpros LIKE '%" + search + "%'"
	                + " OR orgs LIKE '%" + search + "%'"
	                + ";";
	
			ResultSet rs  = stmt.executeQuery(sql);
			int rows = model.getRowCount();
			for(int i = 0;i < rows;i++) {
				model.removeRow(0);
			}
			
			int totalSequenceLength = 0;
			while (rs.next()) {	
				model.addRow(new Object[] {rs.getString("ID"),rs.getString("proteins"),rs.getString("accessions"),rs.getString("sequences"),rs.getString("annotations"),rs.getString("interpros"),rs.getString("orgs")});	   
				totalSequenceLength += rs.getString("sequences").length();
			}
			int resultsCount = model.getRowCount();
			int averageSequenceLength = totalSequenceLength/resultsCount;
			numberOfResultsLabel.setText("Number of results: " + Integer.toString(resultsCount));
			numberOfResultsLabel.setVisible(true);
			averageSequenceLengthLabel.setText("Average sequence length: " + Integer.toString(averageSequenceLength));
			averageSequenceLengthLabel.setVisible(true);
		} catch (SQLException e) {
		}
	}
	
	private static void clearTable() {
		try {
			stmt.execute("DELETE FROM RNA;");
			int rows = model.getRowCount();
			for(int i = 0;i < rows;i++) {
				model.removeRow(0);
			}
		} catch (SQLException e) {
		}
	}
	
	private static void connectDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			c.setAutoCommit(true);
			System.out.println("Opened database successfully");
		}
		catch (Exception ex){
		}
	}
	
	private static void createDBTable() {
		sql = "CREATE TABLE RNA " +
				"(ID INT PRIMARY KEY     NOT NULL," +
				" proteins            TEXT    NOT NULL, " + 
				" accessions          TEXT    NOT NULL, " + 
				" sequences           TEXT    NOT NULL, " + 
				" annotations         TEXT    NOT NULL, " + 
				" interpros           TEXT    NOT NULL, " + 
				" orgs          	  TEXT    NOT NULL)" 
				;
		try {
			stmt.executeUpdate(sql);
			System.out.println("table created");
		}
		catch( Exception ex ){
		}
	}
	
	private static void appendDBFromFile(String filename) {
		if(filename.endsWith("tsv")) {
			try {
				String strCurrentLine;
				objReader = new BufferedReader(new FileReader(filename));
				boolean isFirstLine = true;
				boolean[] columns = {true,false,false,false,false,false,false};
				while ((strCurrentLine = objReader.readLine()) != null) {
					System.out.println("hi");
					if(isFirstLine == false) {
						sql = "INSERT INTO RNA (ID,proteins,accessions,sequences,annotations,interpros,orgs)" + "VALUES (";
						String[] value = strCurrentLine.split("\t",-1);
						int j = 0;
						for(int i = 0; i < 7;i++) {
							if(columns[i] == true) {
								try {
									value[j] = value[j].replace("', '"," ");
								}
								catch (Exception e){
									
								}
								try {
									value[j] = value[j].replace("['","");
								}
								catch (Exception e){
									
								}
								try {
									value[j] = value[j].replace("']","");
								}
								catch (Exception e){
									
								}
								try {
									value[j] = value[j].replace("{'","");
								}
								catch (Exception e){
									
								}
								try {
									value[j] = value[j].replace("'}","");
								}
								catch (Exception e){
									
								}
								if(i != 6) sql = sql + "'" + value[j] + "',";
								else sql = sql + "'" + value[j] + "');";
								j++;
							}
							else {
								if(i != 6) sql = sql + "'" + " " + "',";
								else sql = sql + "'" + " " + "');";
							}
							
						}
						try {
							stmt.executeUpdate(sql);
						}
						catch ( Exception ex) {
							ex.printStackTrace();
						}
					}
					else {
						String value = strCurrentLine;
//						if(value[0] == "index") columns[0] = true;
//						else columns[0] = false;
						if(value.contains("proteins")) columns[1] = true;
						else columns[1] = false;
						if(value.contains("accessions")) columns[2] = true;
						else columns[2] = false;
						if(value.contains("sequences")) columns[3] = true;
						else columns[3] = false;
						if(value.contains("annotations")) columns[4] = true;
						else columns[4] = false;
						if(value.contains("interpros")) columns[5] = true;
						else columns[5] = false;
						if(value.contains("orgs")) columns[6] = true;
						else columns[6] = false;
						isFirstLine = false;
					}
				}
				
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (objReader != null)
						objReader.close();
				} catch (IOException ex) {
					
				}
			}
		}
		
		else JOptionPane.showMessageDialog(f, "Wrong extension! Please use .csv or .tsv formats");
	}
	
	private static void loadTableModelFromDB() {
		try {
			ResultSet rs = stmt.executeQuery( "SELECT * FROM RNA;" );
			int rows = model.getRowCount();
			for(int i = 0;i < rows;i++) {
				model.removeRow(0);
			}
			while ( rs.next() ) {
				model.addRow(new Object[] {rs.getString("ID"),rs.getString("proteins"),rs.getString("accessions"),rs.getString("sequences"),rs.getString("annotations"),rs.getString("interpros"),rs.getString("orgs")});	   
			}
			
			rs.close();
		}
		catch(Exception ex) {
		}
	}
	
	private static void loadGUIFromTableModel(int page) {
		try {
			int rows = tempModel.getRowCount();
			for(int i = 0;i < rows;i++) {
				tempModel.removeRow(0);
			}
			for(int i=page*31;i<(page+1)*31;i++) {
				tempModel.addRow(new Object[] {model.getValueAt(i,0),model.getValueAt(i,1),model.getValueAt(i,2),model.getValueAt(i,3),model.getValueAt(i,4),model.getValueAt(i,5),model.getValueAt(i,6)});
			}
			pageTextField.setText(Integer.toString(pageNumber + 1));
		}
		catch(Exception ex) {
		}
	}
	
	private static void disconnectDB() {
		try {
			stmt.close();
//			c.commit();
			c.close();
		} catch (SQLException e) {
		}
	}


	public static void main(String[] args) {
		
		try {
			connectDB();
			stmt = c.createStatement();
			setupGUI();		
			createDBTable();
			loadTableModelFromDB();
			loadGUIFromTableModel(pageNumber);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		} 
	}
	
}
