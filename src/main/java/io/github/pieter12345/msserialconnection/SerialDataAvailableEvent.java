package io.github.pieter12345.msserialconnection;

import com.laytonsmith.core.events.BindableEvent;

import jssc.SerialPort;

/**
 * Represents a serial data available event.
 * @author P.J.S. Kools
 */
public class SerialDataAvailableEvent implements BindableEvent {
	
	private SerialPort serialPort;
	private int rxBufferByteCount;
	
	/**
	 * Creates a new {@link SerialDataAvailableEvent} for the given serial port and available data count.
	 * @param serialPort - The {@link SerialPort} through which the data was received.
	 * @param rxBufferByteCount - The amount of bytes available in the RX buffer at the time this event was generated.
	 */
	public SerialDataAvailableEvent(SerialPort serialPort, int rxBufferByteCount) {
		this.serialPort = serialPort;
		this.rxBufferByteCount = rxBufferByteCount;
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
	 * Gets the amount of bytes available in the RX buffer at the time this event was generated.
	 * @return The amount of bytes available.
	 */
	public int getRXBufferByteCount() {
		return this.rxBufferByteCount;
	}
}
