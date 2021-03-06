package plugin.experiment.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


import plugin.experiment.configurator.Configurator;
import plugin.experiment.configurator.Core;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CompileHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public CompileHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Compile handler called");
		org.eclipse.ui.PlatformUI.getWorkbench().saveAllEditors(false);

		String returnString = "";
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (Core.getInstance().isCounting()) {
			returnString = Core.getInstance().compile();
			MessageConsole myConsole = findConsole("COMPILADOR");
		    MessageConsoleStream out = myConsole.newMessageStream();
		    if (returnString.isEmpty()) {
		    	returnString = "Não foram encontrados erros de compilação na configuração atual do arquivo";
		    }
		    myConsole.clearConsole();
		    out.println(returnString);		
		    IWorkbenchPage page = workbenchPart.getSite().getPage();// obtain the active page
		    String id = IConsoleConstants.ID_CONSOLE_VIEW;
		    IConsoleView view;
			try {
				view = (IConsoleView) page.showView(id);
				 view.display(myConsole);
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
			
		} else {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openWarning(
					window.getShell(),
					"Experimento",
					"Não é possível compilar o arquivo sem ter iniciado o cronômetro");
		}
		
				
		return null;
	}
	
	private MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	   }
	
	
}
