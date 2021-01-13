package io.github.pieter12345.msserialconnection;

import com.laytonsmith.core.events.BindableEvent;

public class SerialDataEvent implements BindableEvent {
	
	// Variables & Constants.
	private String message;
	private byte[] data;
	
	// Constructor.
	public SerialDataEvent(String message) {
		this.message = message;
		this.data = message.getBytes();
	}
	
	// Constructor.
	public SerialDataEvent(byte[] data) {
		this.message = new String(data);
		this.data = data.clone();
	}
	
	@Override
	public Object _GetObject() {
		return this;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public byte[] getData() {
		return this.data;
	}
}
