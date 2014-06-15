package com.ideaflow.eclipse;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ideaflow.controller.IFMController;
import com.ideaflow.model.TimeService;

/**
 * The activator class controls the plug-in life cycle
 */
public class IdeaFlowActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ideaflow"; //$NON-NLS-1$

	// The shared instance
	private static IdeaFlowActivator plugin;
	
	private IFMController controller;
	private Listener listener;
	
	/**
	 * The constructor
	 */
	public IdeaFlowActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		controller = new IFMController(new TimeService(), new IDEServiceImpl());
		
		ISelectionService ss = getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ss.addPostSelectionListener(new Listener());
		
		Shell shell = getWorkbench().getActiveWorkbenchWindow().getShell();
		shell.addShellListener(new Listener());

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		controller.closeIdeaFlow();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IdeaFlowActivator getDefault() {
		return plugin;
	}
	
	public static IFMController getController() {
		return getDefault().controller;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	private class Listener extends ShellAdapter implements ISelectionListener, ShellListener {

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (part instanceof EditorPart) {
				String fileName = ((EditorPart)part).getEditorInput().getName();
				controller.startFileEvent(fileName);
			} //does not track if file closed
			
		}
		public void shellActivated(ShellEvent event) {
			controller.startFileEventForCurrentFile();
		}
		
		public void shellDeactivated(ShellEvent event) {
			controller.startFileEvent("[[deactivated]]");
		}
		
	}
}
