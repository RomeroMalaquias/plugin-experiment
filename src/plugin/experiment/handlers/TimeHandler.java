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
import plugin.experiment.configurator.Core;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;



/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TimeHandler 
	extends AbstractHandler implements IElementUpdater {
	
	private URL fullPathString;	
	private ImageDescriptor play;
    private ImageDescriptor pause;
    private IWorkbenchPart part;
	private IHandlerService service;
		

	/**
	 * The constructor.
	 */
	public TimeHandler() {
		Core.getInstance().isCounting();
		fullPathString = BundleUtility.find("plugin.experiment", "icons/play.png");	
		play = ImageDescriptor.createFromURL(fullPathString);
		fullPathString = BundleUtility.find("plugin.experiment", "icons/pause.png");
		pause = ImageDescriptor.createFromURL(fullPathString);       
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		System.out.println("Time handler called");
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (Core.getInstance().isDisabled()) {
			MessageDialog.openWarning(
					window.getShell(),
					"Experimento",
					"Tarefa finalizada, siga as instruções do papel!");

		} else { 

		ICommandService commandService = (ICommandService) window.getService(ICommandService.class);
		part = HandlerUtil.getActivePartChecked(event);
		service = (IHandlerService)part.getSite().getService(IHandlerService.class);
	    if (event.getTrigger() == null) {
	    	if (Core.getInstance().isCounting()) {
	    		System.out.println("Testing");
				if (testAnswer()) {			
					MessageDialog.openInformation(
							window.getShell(),
							"Experimento",
							"Tarefa cumprida!");
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
	    	
	    	if (!Core.getInstance().isCounting()) {
	    		Core.getInstance().startCounter();
	    		System.out.println("Starting");
			} else {					
				Core.getInstance().startCounter();
				System.out.println("Pausing");
				MessageDialog.openWarning(
						window.getShell(),
						"Experimento",
						"Tempo pausado");
				
			}
	    }
		
	    if (commandService != null) {
	        commandService.refreshElements("plugin.experiment.commands.timeCommand", null);
	    }		
		}
		
		return null;
	}
	
	@Override
	public void updateElement(UIElement element,@SuppressWarnings("rawtypes") Map arg1) {
		if(Core.getInstance().isCounting() && Core.getInstance().hasStarted()) { 
			System.out.println("pause deveria estar aparecendo");
            element.setIcon(pause);
        } else {
        	System.out.println("play deveria estar aparecendo");
            element.setIcon(play);
        }     
		
	}
	
	private boolean testAnswer() {
		org.eclipse.ui.PlatformUI.getWorkbench().saveAllEditors(false);
				
		return Core.getInstance().testAnswer();
	}
	
	
	
}
