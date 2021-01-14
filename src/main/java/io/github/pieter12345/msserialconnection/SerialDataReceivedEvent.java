package io.github.pieter12345.msserialconnection;

import com.laytonsmith.core.events.BindableEvent;

import jssc.SerialPort;

/**
 * Represents a serial data received event.
 * @author P.J.S. Kools
 */
public class SerialDataReceivedEvent implements BindableEvent {
	
	private SerialPort serialPort;
	private byte[] data;
	
	/**
	 * Creates a new {@link SerialDataReceivedEvent} from the given serial port where the given data was received.
	 * @param serialPort - The {@link SerialPort} through which the data was received.
	 * @param data - The data that was received.
	 */
	public SerialDataReceivedEvent(SerialPort serialPort, byte[] data) {
		this.serialPort = serialPort;
		this.data = data.clone();
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
	
	/**
	 * Gets the data that was received in this event.
	 * @return The received data.
	 */
	public byte[] getData() {
		return this.data;
	}
}
