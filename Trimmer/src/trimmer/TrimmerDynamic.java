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
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingWorker;


public class TrimmerDynamic extends SwingWorker<Integer, String>
{
	private ArrayList<String> maxAL;
	private ArrayList<String> minAL;
	private String[][] arr;
	private File fileOutput;
	private File[] fileIn;
	JLabel label;
	int padding = 0;
	int skip = 1;
	PrintStream ps;

	@Override
	protected Integer doInBackground() throws Exception
	{
		publish("Reading File...");
		arr = csvToArray(fileIn, ",", 2);
		setProgress(30);
		double max_val = Integer.MIN_VALUE;
		double min_val = Integer.MAX_VALUE;
		boolean lookingForMax = true;
		boolean lookingForMin = false;
		publish("Processing...");
		double mid = midpoint();
		for(int r = 29; r < arr.length; r++)
		{ // Run through the whole file line by line
			//Find max value
			if(Double.parseDouble(arr[r][2]) > max_val && lookingForMax && Double.parseDouble(arr[r][2]) > (mid + mid * (5/100)))
			{
				max_val = Double.parseDouble(arr[r][2]);
			}
			else if(Double.parseDouble(arr[r][2]) < max_val && lookingForMax && Double.parseDouble(arr[r][2]) > (mid + mid * (5/100)))
			{
				getAverage(r-1, lookingForMax, padding);
				lookingForMax = false;
				lookingForMin = true;
				max_val = Integer.MIN_VALUE;
			}

			if(Double.parseDouble(arr[r][2]) < min_val && lookingForMin && Double.parseDouble(arr[r][2]) < (mid - mid * (5/100)))
			{
				min_val=Double.parseDouble(arr[r][2]);
			}
			else if(Double.parseDouble(arr[r][2]) > min_val && lookingForMin && Double.parseDouble(arr[r][2]) < (mid - mid * (5/100)))
			{
				getAverage(r-1, lookingForMax, padding);
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
	public double midpoint ()
	//starts iterating through array until it finds a point where the values stop increasing or stop decreasing.
	//then it has found either a min or a max
	//now it searches for the other and computes a mean between the two.
	{
		double mid = 0;
		boolean foundMax = false;
		boolean foundMin = false;
		double max = Integer.MIN_VALUE;
		double min = Integer.MAX_VALUE;
		int r = 29;
		boolean start = true;
		while (!foundMax && !foundMin)
		{
			if (Double.parseDouble(arr[r][2]) > Double.parseDouble(arr[r+1][2]) && start)
			{
				if (min > Double.parseDouble(arr[r][2]))
				{
					min = Double.parseDouble(arr[r][2]);
					r++; 
					continue;
				}
				else
				{
					foundMin = true;
					start = false;
				}
			}
			else if (start)
			{
				if (max < Double.parseDouble(arr[r][2]))
				{
					max = Double.parseDouble(arr[r][2]);
					r++;
					continue;
				}
				else
				{
					foundMax = true;
					start = false;
				}
			}
			if (!foundMax)
			{
				if (max < Double.parseDouble(arr[r][2]))
				{
					max = Double.parseDouble(arr[r][2]);
				}
				else
				{
					foundMax = true;
				}
			}
			if (!foundMin)
			{
				if (min < Double.parseDouble(arr[r][2]))
				{
					min = Double.parseDouble(arr[r][2]);
				}
				else
				{
					foundMin = true;
				}
			}
		}
		mid = (max + min) / 2;
		return mid;
	}
	TrimmerDynamic(File[] files, File out, JLabel label, int pad, int skip) throws IOException
	{
		this.label = label;
		padding = pad;
		fileIn = files;
		fileOutput = out;
		this.skip = skip;
		maxAL = new ArrayList<String>();
		minAL = new ArrayList<String>();
	}
	public void getAverage(int r, boolean max, int bythismany)
	{
		double v2 = 0; 
		String row = "";
		for(int i = r - bythismany; i <= r + bythismany; i++)
		//calculate mean
		{
			
			
			v2+=Double.parseDouble(arr[i][2]);
			
		}
		
		v2 = v2 / ((bythismany * 2) + 1);
		
		
		double var2 = 0;
		
		for(int i = r - bythismany; i <= r + bythismany; i++)
		//calculate variance
		{
			
			
			var2+= Math.pow((Double.parseDouble(arr[i][2]) - v2), 2);
			
		}
		
		var2 = var2 / ((bythismany * 2) + 1);
		
		
		//calculate std deviation
		
		var2 = Math.sqrt(var2);
		
		
		
		double m0 = 0, m1 = 0, m2 = 0, m3 = 0, m4 = 0;
		int count = 0;
		for(int i = r - bythismany; i <= r + bythismany; i++)
			//calculate mean only if data point is within standard deviation
			{
				double dif = Math.abs(v2 - Double.parseDouble(arr[i][2]));
				if (dif  <= var2)
				{
					count++;
					m0+=Double.parseDouble(arr[i][0]);
					m1+=Double.parseDouble(arr[i][1]);
					m2+=Double.parseDouble(arr[i][2]);
					m3+=Double.parseDouble(arr[i][3]);
					m4+=Double.parseDouble(arr[i][4]);
				}
			}
		row = m0/count + "	" + m1/count + "	" + m2/count + "	" + m3/count + "	" + m4/count;
		if(max)
		{
			maxAL.add(row);
		}
		else
		{
			minAL.add(row);
		}
	}

	public void createFile(ArrayList<String> max , ArrayList<String> min, String nameOfnewFile) throws FileNotFoundException
	{
		String toAdd; 
		try
		{
			ps = new PrintStream(new FileOutputStream(new File(fileOutput, nameOfnewFile)));
			for(int i = 0; i<min.size(); i+=skip)
			{
				toAdd = max.get(i) + "	" + min.get(i);
				ps.println(toAdd);
			}
		}
		catch(FileNotFoundException e){
		}
	}
	
	public void closeFile()
	{
		ps.close();
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
	
	
	
	protected void process(final List<String> chunks) 
	{
		for (final String string : chunks) 
		{
			label.setText(string);
		}
	}
}
