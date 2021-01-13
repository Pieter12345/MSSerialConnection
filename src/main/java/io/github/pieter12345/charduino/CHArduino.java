package io.github.pieter12345.charduino;

import java.io.IOException;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

import io.github.pieter12345.arduinoconnection.ArduinoConnection;
import io.github.pieter12345.arduinoconnection.ArduinoConnectionException;
import io.github.pieter12345.arduinoconnection.UnsupportedException;

/**
 * CHArduino class.
 * @author Pieter12345 / Woesh0007
 * @since 14-09-2015
 */
@MSExtension("CHArduino")
public class CHArduino extends AbstractExtension {
	public static ArduinoConnection arduinoConn = null;
	private static ArduinoEventListener listener = new ArduinoEventListener();
	
	@Override
	public void onStartup() {
		try {
			System.out.println("Loading CHArduino " + getVersion() + ".");
			arduinoConn = new ArduinoConnection(9600);
			arduinoConn.addArduinoConnectionListener(listener);
		} catch (ArduinoConnectionException | UnsupportedException | IOException e) { }
		System.out.println("CHArduino " + getVersion() + " loaded. The arduino is " + (arduinoConn == null ? "NOT " : "") + "connected.");
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
