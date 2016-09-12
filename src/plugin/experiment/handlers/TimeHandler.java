package plugin.experiment.handlers;


import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.menus.UIElement;
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
	private Calendar cal;
	private URL fullPathString;	
	private ImageDescriptor play;
    private ImageDescriptor pause;       

	/**
	 * The constructor.
	 */
	public TimeHandler() {
		times = new ArrayList<>();
		isRunning = false;
		lastTime = 0;
		accumulatedTime = 0;
		cal = Calendar.getInstance();
		fullPathString = BundleUtility.find("plugin.experiment", "icons/play.png");	
		play = ImageDescriptor.createFromURL(fullPathString);
		fullPathString = BundleUtility.find("plugin.experiment", "icons/pause.png");
		pause = ImageDescriptor.createFromURL(fullPathString);       
		hasStarted = false;
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ICommandService commandService = (ICommandService) window.getService(ICommandService.class);
	    if (event.getTrigger() == null) {
	    	if (isRunning) {	    		
				accumulatedTime = cal.getTimeInMillis() - lastTime;
				times.add(accumulatedTime);
				if (false) {			
					MessageDialog.openInformation(
							window.getShell(),
							"Experimento",
							"Tarefa cumprida!");
					hasStarted = false;
					isRunning = false;
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
				lastTime = cal.getTimeInMillis();
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
	        commandService.refreshElements("plugin.experiment.commands.sampleCommand", null);
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

}
