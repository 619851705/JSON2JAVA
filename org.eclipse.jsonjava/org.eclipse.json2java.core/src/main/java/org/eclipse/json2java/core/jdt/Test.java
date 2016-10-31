package org.eclipse.json2java.core.jdt;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class Test {
	public static void main(String[] args) throws JavaModelException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		// Loop over all projects
		for (IProject project : projects) {
			if (project.getName().equalsIgnoreCase("org.eclipse.json2java.core")) {
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragment[] packages = javaProject.getPackageFragments();
				for (IPackageFragment mypackage : packages) {
					if(mypackage.getElementName().equalsIgnoreCase("org.eclipse.json2java.core.jdt")) {
						CompilationUnitUtil.createCompilationUnit(mypackage);
					}
				}
			}
		}
	}
}
