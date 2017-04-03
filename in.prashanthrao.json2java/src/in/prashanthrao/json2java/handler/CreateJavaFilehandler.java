package in.prashanthrao.json2java.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import in.prashanthrao.json2java.parser.JSONParser;

public class CreateJavaFilehandler extends AbstractHandler implements IHandler  {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
	    IWorkbenchPage activePage = window.getActivePage();
	    IStructuredSelection selection = (IStructuredSelection) activePage.getSelection();
	    
	    Object firstElement = selection.getFirstElement();
	    if (firstElement instanceof IFile) {
	    	
	    	IFile file = (IFile)firstElement;
	    	IJavaProject javaProject = (IJavaProject) JavaCore.create(file.getProject());

	    	try {
				IPackageFragment[] packages = javaProject.getPackageFragments();
				List<IPackageFragment> elements = new ArrayList<IPackageFragment>();
				for (IPackageFragment mypackage : packages) {
					if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
						if (!mypackage.isDefaultPackage()) {
							elements.add(mypackage);
						}
					}
				}
				
				ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
				ElementListSelectionDialog listSelectionDialog = new ElementListSelectionDialog(activePage.getWorkbenchWindow().getShell(), labelProvider);
				
				listSelectionDialog.setIgnoreCase(true);
				listSelectionDialog.setTitle("Package Selection");
				listSelectionDialog.setMessage("Select a source package");
				listSelectionDialog.setEmptyListMessage("No source packages to select in given project");
				listSelectionDialog.setElements(elements.toArray());
				listSelectionDialog.setMultipleSelection(false);
				listSelectionDialog.setHelpAvailable(false);

				if (listSelectionDialog.open() == Window.OK) {
					IPackageFragment selectedFragment = (IPackageFragment) listSelectionDialog.getFirstResult();
					JSONParser jsonParser = new JSONParser(selectedFragment); 
					jsonParser.parse(file.getLocation().toFile());
				}
				
//				for (IPackageFragment mypackage : packages) {
//					if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
//						if (!mypackage.isDefaultPackage()) {
////							CompilationUnitUtil.createCompilationUnit(mypackage, file);
//							JSONParser jsonParser = new JSONParser(mypackage); 
//				jsonParser.parse(file.getLocation().toFile());
//							
//						}
//					}
//				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
		return null;
	}
}
