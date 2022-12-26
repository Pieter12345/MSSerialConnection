package io.github.pieter12345.msserialconnection;

import com.laytonsmith.core.events.BindableEvent;

import jssc.SerialPort;

/**
 * Represents a serial output buffer empty event.
 * @author P.J.S. Kools
 */
public class SerialOutputBufferEmptyEvent implements BindableEvent {
	
	private SerialPort serialPort;
	
	/**
	 * Creates a new {@link SerialOutputBufferEmptyEvent} for the given serial port.
	 * @param serialPort - The {@link SerialPort}.
	 */
	public SerialOutputBufferEmptyEvent(SerialPort serialPort) {
		this.serialPort = serialPort;
	}
	
	@Override
	public Object _GetObject() {
		return null; // This is not a wrapped event, so return null.
	}
	
	/**
	 * Gets the {@link SerialPort} that has caused this event.
	 * @return The {@link SerialPort}.
	 */
	public SerialPort getSerialPort() {
		return this.serialPort;
	}
}
