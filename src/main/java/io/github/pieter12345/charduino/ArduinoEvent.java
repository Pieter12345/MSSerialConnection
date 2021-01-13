package io.github.pieter12345.charduino;

import com.laytonsmith.core.events.BindableEvent;

public class ArduinoEvent implements BindableEvent {
	
	// Variables & Constants.
	private String message;
	private byte[] data;
	
	// Constructor.
	public ArduinoEvent(String message) {
		this.message = message;
		this.data = message.getBytes();
	}
	
	// Constructor.
	public ArduinoEvent(byte[] data) {
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
