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
		frame = new JFrame();
		frame.setBounds(100, 100, 315, 488);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setForeground(new Color(0, 128, 128));
		progressBar.setBounds(10, 320, 279, 42);
		progressBar.setValue(0);
		frame.getContentPane().add(progressBar);


		fincvgfile = new JButton("Select Final Coverage File/s");
		fincvgfile.setBackground(SystemColor.textHighlightText);
		fincvgfile.setForeground(Color.BLACK);
		fincvgfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxmntmDynamic.isSelected() || chckbxmntmMaximal.isSelected())
				{
					progressBar.setValue(10);
					JFileChooser chooser = new JFileChooser();
					chooser.setMultiSelectionEnabled(true);
					FileNameExtensionFilter filter = new FileNameExtensionFilter(
							"CSV", "CSV", "dat");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog(fincvgfile);
					if (returnVal == chooser.APPROVE_OPTION)
					{
						files = chooser.getSelectedFiles();
						dirBtn.setEnabled(true);
					}
				}
			}	
		});
		fincvgfile.setBounds(45, 25, 196, 50);
		frame.getContentPane().add(fincvgfile);

		dirBtn = new JButton("Select Output Folder");
		dirBtn.setEnabled(false); 
		dirBtn.setBackground(SystemColor.textHighlightText);
		dirBtn.setForeground(Color.BLACK);
		dirBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				strBtn.setEnabled(true);
				progressBar.setValue(30);
				progress = 30;
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
					outputDir = chooser.getSelectedFile();
				}
				else {
					System.out.println("No Selection ");
				}
			}
		});
		dirBtn.setBounds(45, 93, 196, 50);
		frame.getContentPane().add(dirBtn);

		strBtn = new JButton("Start");
		strBtn.setEnabled(false); 
		strBtn.setBackground(SystemColor.textHighlightText);
		strBtn.setForeground(Color.BLACK);
		strBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fincvgfile.setEnabled(false);
				dirBtn.setEnabled(false);
				strBtn.setEnabled(false);
				progress = 40; 
				padding = ((Double) (spinner.getValue())).intValue();
				//number of spaces before and after a selection
				skip = ((Double) (spinner2.getValue())).intValue();
				progressBar.setValue(progress);
				resBtn.setEnabled(true);
				if (chckbxmntmMaximal.isSelected())
				{
					try 
					{
						sub1 = new TrimmerMinMax(files, outputDir, prgLbl);
						sub1.execute();
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

