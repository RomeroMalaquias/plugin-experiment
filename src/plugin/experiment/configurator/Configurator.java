package plugin.experiment.configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

public class Configurator {
private static Configurator instance;
private String taskCommand = "";
private String projectName = "";
private String taskFilename = "";
private String compileCommand1 = "";
private String compileCommand2 = "";
private String compilePart1 = "";
private String compilePart2 = "";
private String taskTitle = "";
private String reportFile = "";
private Properties prop;
private String filename;
private String timeHandler;
private Configurator () {
	prop = new Properties();
	filename = "/config.properties";
	timeHandler = "./config.properties";
	
	InputStream input = getClass().getResourceAsStream(filename);

	try {
		
		if (input == null) {
			System.out.println("Could not read the properties");		
			
		}
		// load a properties file
		prop.load(input);		
		System.out.println(prop.isEmpty());
		taskCommand = prop.getProperty("task.command");//"/home/romero/Mestrado/Experimento2/tests/VIM/task1.sh";
		projectName = prop.getProperty("task.project");//"VIM";
		compileCommand1 = prop.getProperty("compile1");//"/home/romero/Mestrado/Experimento2/tests/VIM/compile.sh";
		compileCommand2 = prop.getProperty("compile2");//"/home/romero/Mestrado/Experimento2/tests/VIM/compile2.sh";
		compilePart1 = prop.getProperty("compile.part1");//"gcc -c /home/romero/Mestrado/Experimento2/workspace/VIM/src/";
		compilePart2 = prop.getProperty("compile.part2");//" /home/romero/Mestrado/Experimento2/workspace/VIM/src/os_unixx.h -w";
		taskFilename = prop.getProperty("task.filename");//"msecWait.c";
		reportFile = prop.getProperty("report.filename");//"/home/romero/Mestrado/Experimento2/report.txt"
		//task2Filename = "regExec.c";
		//task3Filename = "memTest.c";
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

public static Configurator getInstance() {
	if (instance == null) {
		instance = new Configurator();
	}
	
	return instance;
}

public String getTaskCommand() {
	return taskCommand;
}


public String getProjectName() {
	return projectName;
}

public String getTaskFilename() {
	return taskFilename;
}

public String getCompileComand1() {
	return compileCommand1;
}

public String getCompileComand2() {
	return compileCommand2;
}

public String generateCompileParams() {
	System.out.println(compilePart1 + " " + taskFilename + " " + compilePart2);
	return compilePart1 + " " + taskFilename + " " + compilePart2;
}

public String getTaskTitle() {
	return taskTitle;
}

public String getReportFile() {
	return reportFile; //"/home/romero/Mestrado/Experimento2/report.txt"
}

public void setLastTime(String lastTime) {	
	try {
		File file = new File(timeHandler);
		System.out.println("File exists? " + file.exists());
		OutputStream output = new FileOutputStream(file);
		prop.setProperty("time.last", lastTime);
		prop.store(output, null);
	} catch (Exception e) {
		
	}
}

public void setAccumulatedTime(String time) {
	try {
		File file = new File(timeHandler);
		OutputStream output = new FileOutputStream(file);
		prop.setProperty("time.accumulated", time);
		prop.store(output, null);
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}

public long getLastTime() {
	String lastTime = prop.getProperty("time.last");
	long time = 0;
	try {
		time = Long.parseLong(lastTime);
	} catch (Exception e) {
	}
	return time;
}

public long getAccumulatedTime() {
	String accumulatedTime = prop.getProperty("time.accumulated");
	long time = 0;
	try {
		time = Long.parseLong("time.accumulated");
	} catch (Exception e) {
	}
	return time;
}

public void removeTimes(){
	
	try {		
		File file = new File(timeHandler);
		OutputStream output = new FileOutputStream(file);
		prop.remove("time.accumulated");
		prop.remove("time.last");
		prop.store(output, null);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}
