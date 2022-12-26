package io.github.pieter12345.msserialconnection;

import java.util.HashMap;
import java.util.Map;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Main class for the {@link MSSerialConnection} MethodScript extension.
 * @author P.J.S. Kools
 */
@MSExtension("MSSerialConnection")
public class MSSerialConnection extends AbstractExtension {
	
	/**
	 * Contains all presumed open serial connections, having their serial port name as key.
	 */
	public static final Map<String, SerialPort> SERIAL_CONNECTIONS = new HashMap<>();
	
	@Override
	public void onShutdown() {
		
		// Close and remove all serial connections.
		for(SerialPort serialPort : SERIAL_CONNECTIONS.values()) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				// Ignore.
			}
		}
		SERIAL_CONNECTIONS.clear();
	}
	
	@Override
	public Version getVersion() {
		return new SimpleVersion(2, 0, 0);
	}
}
