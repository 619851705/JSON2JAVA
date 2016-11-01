package org.eclipse.json2java.ui.handler;

import java.io.IOException;

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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.json2java.core.jdt.CompilationUnitUtil;
import org.eclipse.json2java.core.parser.JSONParser;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

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
				for (IPackageFragment mypackage : packages) {
					if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
						if (!mypackage.isDefaultPackage()) {
//							CompilationUnitUtil.createCompilationUnit(mypackage, file);
							
							JSONParser jsonParser = new JSONParser(mypackage); 
							jsonParser.parse(file.getLocation().toFile());
						}
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
		return null;
	}
}
