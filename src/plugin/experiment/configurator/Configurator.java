package plugin.experiment.configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

private Configurator () {
	Properties prop = new Properties();
	InputStream input = null;
	String filename = "./config.properties";

	try {
		File file = new File(filename);
		if (!file.exists()) {
			FileOutputStream newFile = new FileOutputStream(file);
			newFile.write(("task.command=/home/romero/Mestrado/Experimento2/tests/VIM/task1.sh \n" +
				"task.project=VIM \n" +
				"compile1=/home/romero/Mestrado/Experimento2/tests/VIM/compile.sh \n" +
				"compile2=/home/romero/Mestrado/Experimento2/tests/VIM/compile2.sh \n" +
				"compile.part1=gcc -c /home/romero/Mestrado/Experimento2/workspace/VIM/src/ \n" +
				"compile.part2=/home/romero/Mestrado/Experimento2/workspace/VIM/src/os_unixx.h -w \n" +
				"task.filename=msecWait.c \n"+
				"report.filename=/home/romero/Mestrado/Experimento2/report.txt").getBytes());
			
		}
		System.out.println(file.exists());
		System.out.println(file.getParent());
		input = new FileInputStream(file);
		
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

public String generateCompileParams(String file) {
	return compilePart1 + file + compilePart2;
}

public String getTaskTitle() {
	return taskTitle;
}

public String getReportFile() {
	return reportFile; //"/home/romero/Mestrado/Experimento2/report.txt"
}
}
