package net.dromard.filesynchronizer.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.dromard.common.util.ReflectHelper;
import net.dromard.filesynchronizer.gui.IconManager;
import net.dromard.filesynchronizer.gui.ManagerListener;
import net.dromard.filesynchronizer.treenode.FileSynchronizerTodoTaskTreeNode;

public class ModuleManager {
	/** Private singleton instance. */
	private static ModuleManager instance = new ModuleManager();
	List<IModule> modules = new ArrayList<IModule>();
	List<ManagerListener> listeners = new ArrayList<ManagerListener>();

	/** Private constructor. */
	private ModuleManager() {
	}
	
	private IModule initialize(IModule module) {
		FileSynchronizerTodoTaskTreeNode.registerTodoTasks(module.retrieveTodoTasks(), module.retrieveTodoTaskNames());
		module.addImageTypes(IconManager.IMAGE_TYPES);
		return module;
	}

	public static ModuleManager getInstance() {
		return instance;
	}
	
	public List<IModule> getAvailableModules() {
		return modules;
	}

	public List<ManagerListener> registerModules() {
		try {
			Set<Class<?>> classes = ReflectHelper.getClasses(this.getClass().getPackage().getName());
			for (Class<?> clazz : classes) {
				Arrays.asList(clazz.getInterfaces()).contains(IModule.class);
				if (ReflectHelper.implement(clazz, IModule.class)) {
					// Register modules
					modules.add(initialize((IModule) ReflectHelper.newInstance(clazz)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Return the listener modules
		for (IModule module : modules) {
			if (module instanceof ManagerListener) {
				listeners.add((ManagerListener) module);
			}
		}
		return listeners;
	}
}
