package org.codescale.fragment.a;

import org.codescale.bundle.internal.a.InternalBundleClass;

/**
 * @author CodeScale
 * 
 */
public class PublicFragmentClass {

	public PublicFragmentClass() {
		InternalBundleClass instance = new InternalBundleClass("/resources/fragment.file");
		instance.loadClass("org.codescale.bundle.b.PublicBundleClass");
	}
}
