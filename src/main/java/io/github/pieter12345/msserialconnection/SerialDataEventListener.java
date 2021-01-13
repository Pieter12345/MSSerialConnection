package io.github.pieter12345.msserialconnection;

import com.laytonsmith.core.events.Driver;
import com.laytonsmith.core.events.EventUtils;

import io.github.pieter12345.arduinoconnection.ArduinoConnectionListener;

public class SerialDataEventListener implements ArduinoConnectionListener {
	
	@Override
	public void inputReceived(byte[] input) {
		EventUtils.TriggerListener(Driver.EXTENSION, "serial_data_received", new SerialDataEvent(input));
	}
}
