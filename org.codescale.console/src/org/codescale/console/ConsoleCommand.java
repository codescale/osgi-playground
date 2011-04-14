package org.codescale.console;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * @author CodeScale
 * 
 * Execute this command on your OSGi Console:
 * 
 * loadClass org.codescale.bundle.a.PublicBundleClass
 * loadClass org.codescale.fragment.a.PublicFragmentClass
 * loadClass org.codescale.bundle.internal.a.InternalBundleClass 
 * instance org.codescale.fragment.a.PublicFragmentClass
 * instance org.codescale.bundle.internal.a.InternalBundleClass
 */
public class ConsoleCommand implements CommandProvider {

	private PackageAdmin packageAdmin;

	public void bindService(PackageAdmin packageAdmin) {
		this.packageAdmin = packageAdmin;
	}

	public void unbindService(PackageAdmin packageAdmin) {
		this.packageAdmin = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		return "---CodeScale Fragment Classloading---\n"
				+ "\tloadClass [fqn] - load the class from the given Full Qualified Name and prints out the info about the bundle that has loaded the class\n"
				+ "\tinstance  [fqn] - load the class from the given Full Qualified Name and tries to instantiate the class\n";
	}

	public void info(Class<?> clazz) {
		Bundle bundle = packageAdmin.getBundle(clazz);

		System.out.println("Symbolic-Name: " + bundle.getSymbolicName());

		// print out exported packages
		ExportedPackage[] exportedPackages = packageAdmin.getExportedPackages(bundle);
		System.out.println("The bundle exported package: ");
		for (ExportedPackage exportedPackage : exportedPackages) {
			System.out.println(" > " + exportedPackage.getName());
		}

		// print out fragments
		Bundle[] fragments = packageAdmin.getFragments(bundle);
		System.out.println("The bundle does have " + fragments.length + " fragments: ");
		for (Bundle fragment : fragments) {
			System.out.println(" > " + fragment.getSymbolicName());
		}
	}

	public void _loadClass(CommandInterpreter ci) {
		Bundle srcBundle = FrameworkUtil.getBundle(getClass());
		String fqn = ci.nextArgument();
		System.out.println("Load the class '" + fqn + "', called by bundle '" + srcBundle.getSymbolicName() + "'.");
		Class<?> clazz = loadClass(fqn);
		info(clazz);
	}

	public void _instance(CommandInterpreter ci) {
		Bundle srcBundle = FrameworkUtil.getBundle(getClass());
		String fqn = ci.nextArgument();
		System.out.println("Instantiate the class '" + fqn + "', called by bundle '" + srcBundle.getSymbolicName()
				+ "'.");
		Class<?> clazz = loadClass(fqn);
		if (clazz == null) {
			System.err.println("Failed to load the class.");
			return;
		}

		try {
			clazz.newInstance();
		} catch (InstantiationException e) {
			System.err.println("The class can not be instantiated. "
					+ "(May not have a default constructor or is abstract or an interface)");
		} catch (IllegalAccessException e) {
			System.err.println("The constructor is not accessable. (May be private)");
		}
	}

	private Class<?> loadClass(String fqn) {
		try {
			Class<?> clazz = getClass().getClassLoader().loadClass(fqn);
			ClassLoader destClassLoader = clazz.getClassLoader();
			Bundle destBundle = packageAdmin.getBundle(clazz);
			if (destBundle != null) {
				System.out.println("The class was loaded by '" + destBundle.getSymbolicName() + "' instance '"
						+ destClassLoader.hashCode() + "'.");
			} else {
				System.out.println("The class wasn't loaded by a Bundle ClassLoader but by '"
						+ destClassLoader.getClass().getName() + "'.");
			}
			return clazz;
		} catch (ClassNotFoundException e) {
			System.err.println("Failed to load the class.");
		}
		return null;
	}
}
