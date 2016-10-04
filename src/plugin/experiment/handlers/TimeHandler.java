package plugin.experiment.handlers;


import java.io.BufferedReader;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.menus.UIElement;
import plugin.experiment.configurator.Configurator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;



/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TimeHandler 
	extends AbstractHandler implements IElementUpdater {
	private long lastTime;
	private long accumulatedTime;
	private boolean isRunning;
	private boolean hasStarted;
	private ArrayList<Long> times;
	private URL fullPathString;	
	private ImageDescriptor play;
    private ImageDescriptor pause;
    private IWorkbenchPart part;
	private IHandlerService service;
	private String command;
	

	/**
	 * The constructor.
	 */
	public TimeHandler() {
		times = new ArrayList<>();
		isRunning = false;
		lastTime = 0;
		accumulatedTime = 0;
		fullPathString = BundleUtility.find("plugin.experiment", "icons/play.png");	
		play = ImageDescriptor.createFromURL(fullPathString);
		fullPathString = BundleUtility.find("plugin.experiment", "icons/pause.png");
		pause = ImageDescriptor.createFromURL(fullPathString);       
		hasStarted = false;
		command = Configurator.getInstance().getTaskCommand();
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		System.out.println("Time handler called");
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ICommandService commandService = (ICommandService) window.getService(ICommandService.class);
		part = HandlerUtil.getActivePartChecked(event);
		service = (IHandlerService)part.getSite().getService(IHandlerService.class);
	    if (event.getTrigger() == null) {
	    	if (isRunning) {	    		
				accumulatedTime = System.currentTimeMillis() - lastTime;
				times.add(accumulatedTime);
				if (testAnswer()) {			
					MessageDialog.openInformation(
							window.getShell(),
							"Experimento",
							"Tarefa cumprida!");
					hasStarted = false;
					isRunning = false;
					report();			
					
				} else {
					MessageDialog.openError(
							window.getShell(),
							"Experimento",
							"A tarefa ainda não foi cumprida, tente novamente");			
				}
	    	} else {
	    		MessageDialog.openWarning(
						window.getShell(),
						"Experimento",
						"Não é possível finalizar a tarefa sem ter iniciado o cronômetro");
		
	    	}
	    	
	    } else {
	    	if (!isRunning) {
	    		hasStarted = true;
				lastTime = System.currentTimeMillis();
				isRunning = true;
			} else {						
				MessageDialog.openWarning(
						window.getShell(),
						"Experimento",
						"Tempo pausado");
				isRunning = false;
				
			}
	    }
	    if (commandService != null) {
	        commandService.refreshElements("plugin.experiment.commands.timeCommand", null);
	    }		
	    	
		return null;
	}
	
	@Override
	public void updateElement(UIElement element,@SuppressWarnings("rawtypes") Map arg1) {
		if(isRunning && hasStarted) { 
            element.setIcon(pause);
        } else {
            element.setIcon(play);
        }     
		
	}
	
	private boolean testAnswer() {
		org.eclipse.ui.PlatformUI.getWorkbench().saveAllEditors(false);
		boolean passed = true;
		String out = "";
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
				  out += line + "\n";
				  passed = false;
				}
				while ((line = b2.readLine()) != null) {
					  out += line + "\n";
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
		
		return passed;
	}
	
	public void report() {
		String savestr = Configurator.getInstance().getReportFile();
		String toAppend = Configurator.getInstance().getProjectName() + " - " +Configurator.getInstance().getTaskTitle() + "\n";
		long total = 0;
		toAppend += "Tries: " + times.size() + "\n";
		for (int i = 0; i < times.size(); i++) {
			toAppend += i + ": " + times.get(i) + "\n";
			total += times.get(i);
		}
		toAppend += "Total: " + total + "\n\n";
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
