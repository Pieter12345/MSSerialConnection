package io.github.pieter12345.msserialconnection;

import java.util.Map;
import java.util.TreeMap;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CByteArray;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.events.AbstractEvent;
import com.laytonsmith.core.events.BindableEvent;
import com.laytonsmith.core.events.Driver;
import com.laytonsmith.core.exceptions.EventException;
import com.laytonsmith.core.exceptions.PrefilterNonMatchException;
import com.laytonsmith.core.natives.interfaces.Mixed;

/**
 * Container class for serial connection MethodScript events.
 * @author P.J.S. Kools
 */
@SuppressWarnings("checkstyle:TypeName") // By convention, classes are named after the MS events that they represent.
public class SerialConnectionEvents {
	
	@api
	public static class serial_data_received extends AbstractEvent {
		
		@Override
		public String getName() {
			return "serial_data_received";
		}
		
		@Override
		public String docs() {
			return "Fired when data is received through a serial connection."
					+ " Format: {serialport: The serial port name (string), data: The raw received data (byte_array)}.";
		}
		
		@Override
		public BindableEvent convert(CArray manualObject, Target t) {
			return null;
		}
		
		@Override
		public Driver driver() {
			return Driver.EXTENSION;
		}
		
		@Override
		public Map<String, Mixed> evaluate(BindableEvent event) throws EventException {
			Map<String, Mixed> map = new TreeMap<>();
			if(event instanceof SerialDataReceivedEvent) {
				SerialDataReceivedEvent serialDataReceivedEvent = (SerialDataReceivedEvent) event;
				map.put("serialport", new CString(serialDataReceivedEvent.getSerialPort().getPortName(), null));
				map.put("data", CByteArray.wrap(serialDataReceivedEvent.getData(), null));
			} else {
				throw new EventException(
						"Cannot convert event to " + SerialDataReceivedEvent.class.getSimpleName() + ".");
			}
			return map;
		}
		
		@Override
		public boolean matches(Map<String, Mixed> prefilter, BindableEvent event) throws PrefilterNonMatchException {
			return true;
		}
		
		@Override
		public boolean modifyEvent(String key, Mixed value, BindableEvent event) {
			return false;
		}
		
		@Override
		public Version since() {
			return MSVersion.V3_3_1;
		}
	}
}
