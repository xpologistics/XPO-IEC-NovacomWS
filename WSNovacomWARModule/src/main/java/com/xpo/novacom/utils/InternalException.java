package com.xpo.novacom.utils;

public class InternalException extends Exception {

	private static final long serialVersionUID = 696178139426669998L;

	public InternalException() {
		super();
	}

	public InternalException(final String msg) {
		super(msg);
	}

	public InternalException(final Throwable e) {
		super(e);
	}

	public InternalException(final String msg, final Throwable e) {
		super(msg, e);
	}

}
