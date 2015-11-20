package org.agecraft.extendedmetadata;

public class DeprecatedMethodException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DeprecatedMethodException(int i) {
		super(i == 1 ? "Constructor has been deprecated by ExtendedMetadata" : "Method has been deprecated by ExtendedMetadata");
	}
}
