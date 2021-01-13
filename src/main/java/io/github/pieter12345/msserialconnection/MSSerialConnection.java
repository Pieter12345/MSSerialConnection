package io.github.pieter12345.msserialconnection;

import java.io.IOException;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

import io.github.pieter12345.arduinoconnection.ArduinoConnection;
import io.github.pieter12345.arduinoconnection.ArduinoConnectionException;
import io.github.pieter12345.arduinoconnection.UnsupportedException;

/**
 * MSSerialData class.
 * @author Pieter12345 / Woesh0007
 * @since 14-09-2015
 */
@MSExtension("MSSerialConnection")
public class MSSerialConnection extends AbstractExtension {
	public static ArduinoConnection arduinoConn = null;
	private static SerialDataEventListener listener = new SerialDataEventListener();
	
	@Override
	public void onStartup() {
		try {
			System.out.println("Loading " + MSSerialConnection.class.getSimpleName() + " " + getVersion() + ".");
			arduinoConn = new ArduinoConnection(9600);
			arduinoConn.addArduinoConnectionListener(listener);
		} catch (ArduinoConnectionException | UnsupportedException | IOException e) { }
		System.out.println(MSSerialConnection.class.getSimpleName() + " " + getVersion()
				+ " loaded. The arduino is " + (arduinoConn == null ? "NOT " : "") + "connected.");
	}
	
	@Override
	public void onShutdown() {
		if(arduinoConn != null) {
			arduinoConn.removeArduinoConnectionListener(listener);
			arduinoConn.close();
			
			// Make sure the RXTXSerial library unloads (by destroying the class and therefor its classloader).
			arduinoConn = null;
			System.gc();
		}
		System.out.println("CHArduino " + getVersion() + " unloaded.");
	}
	
	@Override
	public Version getVersion() {
		return new SimpleVersion(0, 0, 1);
	}
	
	public static void reconnect() throws ArduinoConnectionException, UnsupportedException, IOException {
		if(arduinoConn != null) {
			arduinoConn.removeArduinoConnectionListener(listener);
			arduinoConn.close();
		}
		arduinoConn = new ArduinoConnection(9600);
		arduinoConn.addArduinoConnectionListener(listener);
	}
	
	public static void disconnect() {
		if(arduinoConn != null) {
			arduinoConn.removeArduinoConnectionListener(listener);
			arduinoConn.close();
		}
	}
}
