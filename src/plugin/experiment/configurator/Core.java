package plugin.experiment.configurator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Core {
private static Core instance;
private boolean isRunning;
private boolean hasStarted;
private int compiledTimes;
private int triedTimes;
private int pausedTimes;
private long lastTime;
private long accumulatedTime;
private boolean disabled = false;
private boolean firstReport = true;
private enum ACTION { COMPILE, TRY, PAUSED, STARTED, RESTARTED};


	private Core() {
		
		lastTime = Configurator.getInstance().getLastTime();
		accumulatedTime = Configurator.getInstance().getAccumulatedTime();
		isRunning = false;
		pausedTimes = 0;
		if (accumulatedTime != 0 && lastTime !=0) {
			hasStarted = true;
		} else {
			hasStarted = false;
		}
		
		System.out.println(lastTime);
		System.out.println(accumulatedTime);
		
		
	}
	public static Core getInstance() {
		if (instance == null) {
			instance = new Core();
		}
		return instance;
	}
	
	public void recordTime() {
		
	}
	
	public void startCounter() {
		if (!isRunning && !hasStarted) { //Iniciou a contagem
			if (lastTime != 0) {
				System.out.println("There was a restart");
				accumulatedTime += System.currentTimeMillis() - lastTime;				
			}
			lastTime = System.currentTimeMillis();
			Configurator.getInstance().setLastTime(lastTime + "");
			isRunning = true;		
			hasStarted = true;
			reportAction(ACTION.STARTED, System.currentTimeMillis() - lastTime);
		} else if (!isRunning && hasStarted){ //Reiniciou
			reportAction(ACTION.RESTARTED, System.currentTimeMillis() - lastTime);
			isRunning = true;
			lastTime = System.currentTimeMillis();
			Configurator.getInstance().setLastTime(lastTime + "");
		} else { //Pausou
			isRunning = false;
			accumulatedTime += System.currentTimeMillis() - lastTime;
			Configurator.getInstance().setAccumulatedTime(accumulatedTime + "");
			reportAction(ACTION.PAUSED, System.currentTimeMillis() - lastTime);
		}
		
	}
	public boolean isCounting() {
		return isRunning;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public boolean hasStarted() {
		return hasStarted;
	}
	public String compile() {
		reportAction(ACTION.COMPILE, System.currentTimeMillis() - lastTime);
		String out = "";
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec(Configurator.getInstance().getCompileComand1());
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p = r.exec(Configurator.getInstance().generateCompileParams());			
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader b2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line = "";

			try {
				while ((line = b.readLine()) != null) {
				  out += line + "\n";
				}
				while ((line = b2.readLine()) != null) {
					  out += line + "\n";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				b.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p = r.exec(Configurator.getInstance().getCompileComand2());
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return out;
	}
	
	public boolean testAnswer() {
		accumulatedTime += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		Configurator.getInstance().setLastTime(lastTime + "");
		Configurator.getInstance().setAccumulatedTime(accumulatedTime + "");
		
		String command = Configurator.getInstance().getTaskCommand();
		boolean passed = true;
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec(command);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader b2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line = "";

			try {
				while ((line = b.readLine()) != null) {
				  passed = false;
				}
				while ((line = b2.readLine()) != null) {
					  passed = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				b.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				b2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (passed) {
			report();
			isRunning = false;
			disabled = true;
		} else {
			reportAction(ACTION.TRY, accumulatedTime);
		}
		return passed;
	}
	public void reportAction(ACTION action, long time) {
		String savestr = Configurator.getInstance().getReportFile();
		String toAppend = "";
		if (firstReport) {
			firstReport = false;
			toAppend = Configurator.getInstance().getProjectName() + "\n";
		}
		if (action == ACTION.COMPILE) {
			compiledTimes ++;
		} else if (action == ACTION.TRY){
			triedTimes++;
		} else if (action == ACTION.PAUSED) {
			pausedTimes ++;
		}
		toAppend += "Action: " + action.name() + " Time: " + time + "\n";
		if (action == ACTION.PAUSED || action == ACTION.RESTARTED) {
			toAppend += "Accumulated time: " + accumulatedTime + "\n";
		}
		File f = new File(savestr);

		PrintWriter out = null;
		if ( f.exists() && !f.isDirectory() ) {
		    try {
				out = new PrintWriter(new FileOutputStream(new File(savestr), true));
				out.append(toAppend);
			    out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
		else {
		    try {
				out = new PrintWriter(savestr);
				out.println(toAppend);
			    out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
	}
	public void report() {
		String savestr = Configurator.getInstance().getReportFile();
		String toAppend = "";
		if (firstReport) {
			firstReport = false;
			toAppend = Configurator.getInstance().getProjectName() + "\n";
		}
		toAppend += "Action: FINISHED Time: " + lastTime + "\n";
		
		toAppend += "Compiled: " + compiledTimes + "\n"
				+ "Paused: " + pausedTimes + "\n"
				+ "Tries: " + triedTimes + "\n"
				+ "TotalTime: " + accumulatedTime + "\n\n";
		File f = new File(savestr);

		PrintWriter out = null;
		if ( f.exists() && !f.isDirectory() ) {
		    try {
				out = new PrintWriter(new FileOutputStream(new File(savestr), true));
				out.append(toAppend);
			    out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
		else {
		    try {
				out = new PrintWriter(savestr);
				out.println(toAppend);
			    out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
	}
}
