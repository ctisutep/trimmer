package trimmer;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.UIManager;
import javax.swing.JSeparator;
import javax.swing.JLabel;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker.StateValue;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;


public class Frontend {

	private JFrame frame;
	int padding = 0;
	int skip = 1;
	JButton fincvgfile;
	JButton dirBtn;
	JButton strBtn;
	JLabel prgLbl;
	JTextArea textArea = new JTextArea();
	JSpinner spinner;
	JSpinner spinner2;
	JButton resBtn = new JButton("View Results");
	JCheckBoxMenuItem chckbxmntmMaximal;
	JCheckBoxMenuItem chckbxmntmDynamic;
	TrimmerDynamic sub2;
	TrimmerMinMax sub1;
	int progress = 0;
	String[] country;
	File outputDir;
	BufferedReader br;
	File[] files;
	JButton stopButton;
	private String[][] array;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//set the main window
					Frontend window = new Frontend();
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
	public Frontend() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	public static void set(JTextArea a, File file) throws IOException
	{
		//TODO: erase this method, I think it isn't used
		String things = "";
		String curr = "";
		String line = "";
		String cvsSplitBy = ",";
		String[] b = null;
		BufferedReader br = null;
		PrintWriter writer = new PrintWriter("the-file-name.txt");
		br = new BufferedReader(new FileReader(file));
		while ((curr = br.readLine()) != null) 
		{
			writer.println(curr);
			// use comma as separator
			b = curr.split(cvsSplitBy);
			for (int i = 0; i < b.length; i++)
			{
				if (i % 2 == 1)
				{
					line += b[i] + "     ";
				}
				else
				{
					things += b[i] + "     ";
				}
				a.setText(things + "\n" + line);
			}
		}
		writer.close();
		br.close();
	}
	public static String[] text(File file) throws IOException, FileNotFoundException
	{
		//TODO: also not used method
		String line = "";
		String cvsSplitBy = ",";
		String[] country = null;
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) 
		{

			// use comma as separator
			country = line.split(cvsSplitBy); 
		}
		br.close();
		return (country);
	}
	private void initialize() {
		//this method is called to start the main window.
		frame = new JFrame();
		//the size is fixed for now
		frame.setBounds(100, 100, 315, 488);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		final JProgressBar progressBar = new JProgressBar();
		//for some reason this needs to be final on some java versions
		progressBar.setForeground(new Color(0, 128, 128));
		progressBar.setBounds(10, 320, 279, 42);
		//start the progress at 0
		progressBar.setValue(0);
		frame.getContentPane().add(progressBar);

		//create the button to select the file
		fincvgfile = new JButton("Select Final Coverage File/s");
		fincvgfile.setBackground(SystemColor.textHighlightText);
		fincvgfile.setForeground(Color.BLACK);
		//set the action of the button when it is pressed
		fincvgfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//this button only works if you have selected a method type. (max or dynamic)
				//TODO: add a warning if it is clicked without selecting method
				if (chckbxmntmDynamic.isSelected() || chckbxmntmMaximal.isSelected())
				{
					progressBar.setValue(10);
					JFileChooser chooser = new JFileChooser();
					//can select multiple files
					chooser.setMultiSelectionEnabled(true);
					//accepts csv's and dat files
					FileNameExtensionFilter filter = new FileNameExtensionFilter(
							"CSV", "CSV", "dat");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog(fincvgfile);
					if (returnVal == chooser.APPROVE_OPTION)
					{
						//all files are stored in an array of files
						files = chooser.getSelectedFiles();
						//once files are selected, you can select the output directory
						dirBtn.setEnabled(true);
					}
				}
			}	
		});
		fincvgfile.setBounds(45, 25, 196, 50);
		frame.getContentPane().add(fincvgfile);
		
		//output button starts out disabled
		dirBtn = new JButton("Select Output Folder");
		dirBtn.setEnabled(false); 
		dirBtn.setBackground(SystemColor.textHighlightText);
		dirBtn.setForeground(Color.BLACK);
		dirBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Once it is clickd, update progress
				progressBar.setValue(30);
				//select the directory of the output, starts in the directory of the original file
				JFileChooser chooser = new JFileChooser(); 
				chooser.setCurrentDirectory(files[0]);
				chooser.setDialogTitle("Output");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//
				// disable the "All files" option.
				//
				chooser.setAcceptAllFileFilterUsed(false);
				//
				if (chooser.showOpenDialog(dirBtn) == JFileChooser.APPROVE_OPTION) { 
					//once it is selected we are ready to start
					outputDir = chooser.getSelectedFile();
					strBtn.setEnabled(true);
				}
				else {
					System.out.println("No Selection ");
				}
			}
		});
		dirBtn.setBounds(45, 93, 196, 50);
		frame.getContentPane().add(dirBtn);
		
		//start button also starts out disabled
		strBtn = new JButton("Start");
		strBtn.setEnabled(false); 
		strBtn.setBackground(SystemColor.textHighlightText);
		strBtn.setForeground(Color.BLACK);
		strBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//disable all other buttons once the process has started
				fincvgfile.setEnabled(false);
				dirBtn.setEnabled(false);
				strBtn.setEnabled(false);
				progress = 40; 
				//number of spaces before and after a selection
				padding = ((Double) (spinner.getValue())).intValue();
				//skip is the number of lines at the beginning that don't contain data
				skip = ((Double) (spinner2.getValue())).intValue();
				progressBar.setValue(progress);
				//add a reset button to restart or cancel the progress
				resBtn.setEnabled(true);
				//detect which of the two methods will be used.
				if (chckbxmntmMaximal.isSelected())
				{
					try 
					{
						/*
						 * both methods are objects of a swingworker class that does the work on another
						 * thread so the main GUI doesnt hang or get stuck. this class also has methods
						 * to get live progress and to cancel the operation 
						 */
						sub1 = new TrimmerMinMax(files, outputDir, prgLbl);
						//execute calls the doInBackground in the other class.
						sub1.execute();
						//this listener looks at updates generated by the process method in the other class
						sub1.addPropertyChangeListener(new PropertyChangeListener()
						{
							@Override
							public void propertyChange(final PropertyChangeEvent event)
							{
								switch (event.getPropertyName())
								{
								case "progress":
									progressBar.setIndeterminate(false);
									//progressBar.setValue(40 + ((Integer)(event.getNewValue()) ) / 60);
									break;
								case "state":
									switch ((StateValue) event.getNewValue())
									{
									case DONE:
										progressBar.setValue(100);
										stopButton.setVisible(true);
										break;
									case STARTED:
									case PENDING:
										progressBar.setVisible(true);
										progressBar.setIndeterminate(true);
										break;
									}
									break;
								}
							}
						});
					} 
					catch (IOException e1) 
					{
						e1.printStackTrace();
					}
				}
				//basically does the same but with the other method
				else if (chckbxmntmDynamic.isSelected())
				{
					try {
						sub2 = new TrimmerDynamic(files, outputDir, prgLbl, padding, skip);
						sub2.execute();
						sub2.addPropertyChangeListener(new PropertyChangeListener()
						{
							@Override
							public void propertyChange(final PropertyChangeEvent event)
							{
								switch (event.getPropertyName())
								{
								case "progress":
									progressBar.setIndeterminate(false);
									event.get
									//progressBar.setValue(40 + ((Integer)(event.getNewValue()) ) / 60);
									break;
								case "state":
									switch ((StateValue) event.getNewValue())
									{
									case DONE:
										progressBar.setValue(100);
										stopButton.setVisible(true);
										break;
									case STARTED:
									case PENDING:
										progressBar.setVisible(true);
										progressBar.setIndeterminate(true);
										break;
									}
									break;
								}
							}
						});
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		strBtn.setBounds(45, 154, 196, 50);
		frame.getContentPane().add(strBtn);

		JSeparator separator = new JSeparator();
		separator.setBounds(45, 308, 200, 14);
		frame.getContentPane().add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(45, 215, 200, 14);
		frame.getContentPane().add(separator_1);
		
		//this label is passed to the other classes to change the text while the file is being processed
		prgLbl = new JLabel("...");
		prgLbl.setHorizontalAlignment(SwingConstants.CENTER);
		prgLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
		prgLbl.setBounds(45, 234, 196, 63);
		frame.getContentPane().add(prgLbl);
		
		stopButton = new JButton(new AbstractAction("Reset") {

	        @Override
	        public void actionPerformed(ActionEvent arg0) { 
	        	fincvgfile.setEnabled(true);
	        	progressBar.setValue(0);
	        	prgLbl.setText("...");
	        	stopButton.setVisible(false);
	        	files = null;
	        	//closeFile cancels the operation if it is still in progress
	        	if (chckbxmntmMaximal.isSelected())
	        	{
	        		sub1.closeFile();
	        	}
	        	else
	        		sub2.closeFile();
	        	chckbxmntmMaximal.setSelected(false);
	        	chckbxmntmDynamic.setSelected(false);
	        }

	    });
		//not sure if this is used
		stopButton.setBounds(103, 381, 89, 23);
		stopButton.setVisible(false);
		frame.getContentPane().add(stopButton);
		

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Method");
		menuBar.add(mnNewMenu);

		chckbxmntmMaximal = new JCheckBoxMenuItem("Maximal");
		mnNewMenu.add(chckbxmntmMaximal);


		chckbxmntmDynamic = new JCheckBoxMenuItem("Dynamic");
		mnNewMenu.add(chckbxmntmDynamic);
		
		//number of files doesn't actually need to be specified
		//TODO: remove this or change it to another function
		JLabel lblFiles = new JLabel("# of Files");
		menuBar.add(lblFiles);
		
		SpinnerNumberModel model1 = new SpinnerNumberModel(1.0, 1.0, 10.0, 1.0);  
		spinner = new JSpinner(model1);
		menuBar.add(spinner);
		
		JLabel lblSkip = new JLabel("Every n Cycle");
		menuBar.add(lblSkip);
		
		SpinnerNumberModel model2 = new SpinnerNumberModel(1.0, 1.0, 500.0, 1.0);  
		spinner2 = new JSpinner(model2);
		menuBar.add(spinner2);
		
		//set the checkboxes to act as radio buttons
		chckbxmntmDynamic.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if (chckbxmntmMaximal.isSelected())
				{
					chckbxmntmMaximal.setSelected(false);
				}
			}
		});
		chckbxmntmMaximal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if (chckbxmntmDynamic.isSelected())
				{
					chckbxmntmDynamic.setSelected(false);
				}
			}
		});

	}
}

