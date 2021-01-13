package io.github.pieter12345.charduino;

import com.laytonsmith.core.events.Driver;
import com.laytonsmith.core.events.EventUtils;

import io.github.pieter12345.arduinoconnection.ArduinoConnectionListener;

public class ArduinoEventListener implements ArduinoConnectionListener {
	
	@Override
	public void inputReceived(byte[] input) {
		EventUtils.TriggerListener(Driver.EXTENSION, "arduino_data_received", new ArduinoEvent(input));
	}
}
