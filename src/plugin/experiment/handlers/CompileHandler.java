package plugin.experiment.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


import plugin.experiment.configurator.Configurator;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

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
		if (workbenchPart.getSite().getPage().getActiveEditor() != null &&
				workbenchPart.getSite().getPage().getActiveEditor().getEditorInput() != null) {
			returnString = compile(workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getName());
		}
		
		MessageConsole myConsole = findConsole("COMPILADOR");
	    MessageConsoleStream out = myConsole.newMessageStream();
	    if (returnString.isEmpty()) {
	    	returnString = "Não foram encontrados erros de compilação";
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
	
	private String compile(String file) {
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
			p = r.exec(Configurator.getInstance().generateCompileParams(file));			
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
	
}
