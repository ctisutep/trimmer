package trimmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingWorker;


public class TrimmerMinMax extends SwingWorker<Integer, String>
{
	private ArrayList<String> maxAL;
	private ArrayList<String> minAL;
	private String[][] arr;
	private File outputDir;
	private File[] fileIn;
	JLabel label;
	PrintStream ps;

	public TrimmerMinMax(File[] file, File out, JLabel label) throws IOException
	{
		this.label = label;
		fileIn = file;
		maxAL = new ArrayList<String>();
		minAL = new ArrayList<String>();
		outputDir = out;
	}
	public void getAverage(int r, boolean max)
	{
		double[] v = {0,0,0,0,0,0,0,0,0};
		String row = "";
		
		for(int i = r-2; i<=r+2; i++)
		{
			if (arr[i].length < 5)
			{
				v[0] += Double.parseDouble(arr[i][0]);
				v[1] += Double.parseDouble(arr[i][1]);
				v[2] += Double.parseDouble(arr[i][2]);
				v[3] += Double.parseDouble(arr[i][3]);
			}
			else if (arr[i].length == 5)
			{
				v[0] += Double.parseDouble(arr[i][0]);
				v[1] += Double.parseDouble(arr[i][1]);
				v[2] += Double.parseDouble(arr[i][2]);
				v[3] += Double.parseDouble(arr[i][3]);
				v[4] += Double.parseDouble(arr[i][4]);
			}
			else
			{
				v[0] += Double.parseDouble(arr[i][0]);
				v[1] += Double.parseDouble(arr[i][1]);
				v[2] += Double.parseDouble(arr[i][2]);
				v[3] += Double.parseDouble(arr[i][3]);
				v[4] += Double.parseDouble(arr[i][4]);
				v[5] += Double.parseDouble(arr[i][5]);
				v[6] += Double.parseDouble(arr[i][6]);
				v[7] += Double.parseDouble(arr[i][7]);
				v[8] += Double.parseDouble(arr[i][8]);
			}
		}
		if (arr[0].length < 5)
		{
			v[0] =v [0] / 5;
			v[1] =v [1] / 5;
			v[2] =v [2] / 5;
			v[3] =v [3] / 5;
			row = v[0] + "	" + v[1] + "	" + v[2] + "	" + v[3];
		}
		else if (arr[0].length == 5)
		{
			v[0] =v [0] / 5;
			v[1] =v [1] / 5;
			v[2] =v [2] / 5;
			v[3] =v [3] / 5;
			v[4] =v [4] / 5;
			row = v[0] + "	" + v[1] + "	" + v[2] + "	" + v[3] + "	" + v[4];
		}
		else
		{
			v[0] =v [0] / 5;
			v[1] =v [1] / 5;
			v[2] =v [2] / 5;
			v[3] =v [3] / 5;
			v[4] =v [4] / 5;
			v[5] =v [5] / 5;
			v[6] =v [6] / 5;
			v[7] =v [7] / 5;
			v[8] =v [8] / 5;
			row = v[0] + "	" + v[1] + "	" + v[2] + "	" + v[3] + "	" + v[4] + "	" + v[5] + "	" + v[6] + "	" + v[7]+"	"+v[8];
		}
		if(max == true)
		{
			maxAL.add(row);
		}
		else
		{
			minAL.add(row);
		}
	}
	public void createFile(ArrayList<String> max , ArrayList<String> min, String nameOfnewFile) throws FileNotFoundException{
		String toAdd; 
		try{
			ps = new PrintStream(new FileOutputStream(new File(outputDir, nameOfnewFile)));
			for(int i = 0; i < min.size(); i++)
			{
				toAdd = max.get(i) + "	" + min.get(i);
				ps.println(toAdd);
			}
		}
		catch(FileNotFoundException e){
		}
	}

	public String[][] csvToArray(File[] file, String delimiter, int ignoreLines) throws FileNotFoundException{
		String[] line = null;
		ArrayList<String[]> completeList = new ArrayList<String[]>();
		for(int i = 0; i < file.length; i++){
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file[i])));
			line = null;
			try {
				for(int j = 0; j < ignoreLines; j++){
					reader.readLine();
				}
				while((line = reader.readLine().split(delimiter)) != null){
					completeList.add(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
		
		return completeList.toArray(new String[30000][10]);
	}
	
	@Override
	protected Integer doInBackground() throws Exception
	{
		publish("Reading File...");
		arr = csvToArray(fileIn, ",", 2);
		setProgress(30);
		double max_val = Integer.MIN_VALUE;
		int max_val_ind = 0;
		double min_val = Integer.MAX_VALUE;
		boolean lookingForMax = true;
		boolean lookingForMin = false;
		publish("Processing...");
		for(int r = 0; r < arr.length; r++)
		{
			if(Double.parseDouble(arr[r][2]) > max_val && lookingForMax && Double.parseDouble(arr[r][2]) > 800)
			{
				max_val = Double.parseDouble(arr[r][2]);
				max_val_ind = r;
			}
			else if(Double.parseDouble(arr[r][2]) < max_val && lookingForMax && Double.parseDouble(arr[r][2]) > 800){
				getAverage(max_val_ind, lookingForMax);
				lookingForMax = false;
				lookingForMin = true;
				max_val = 0;
			}

			if(lookingForMin)
			{
				r += 75; 
				getAverage(r, lookingForMax);
				lookingForMin = false;
				lookingForMax = true;
			}
			else if(Double.parseDouble(arr[r][2]) > min_val && lookingForMin){
				getAverage(r, lookingForMax);
				lookingForMax = true;
				lookingForMin = false;
				min_val = Integer.MAX_VALUE;
			}
			setProgress(30 + (30 / arr.length) * r );
		}
		setProgress(60);
		publish("Writing File...");
		String newt = fileIn[0].getName();
		int dot = newt.indexOf('.');
		String newName = newt.substring(0,  dot) + "-trimmed.csv";
		createFile(maxAL, minAL, newName);
		publish("Finished");
		setProgress(100);
		return (arr.length);
	}
	@Override
	protected void process(final List<String> chunks) 
	{
		for (final String string : chunks) 
		{
			label.setText(string);
		}
	}
	public void closeFile()
	{
		ps.close();
	}
}

