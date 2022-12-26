package io.github.pieter12345.msserialconnection;

import java.util.Map;
import java.util.TreeMap;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CInt;
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
//By convention, classes are named after the MS events that they represent.
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:HideUtilityClassConstructor"})
public class SerialConnectionEvents {
	
	public static String docs() {
		return "Contains events related to serial connections.";
	}
	
	@api
	public static class serial_data_available extends AbstractEvent {
		
		@Override
		public String getName() {
			return "serial_data_available";
		}
		
		@Override
		public String docs() {
			return "{} Fired when data is received through a serial connection."
					+ " {string serialport: The serial port name | byte_array data: The raw received data} {}";
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
			if(event instanceof SerialDataAvailableEvent) {
				SerialDataAvailableEvent serialDataReceivedEvent = (SerialDataAvailableEvent) event;
				map.put("serialport", new CString(serialDataReceivedEvent.getSerialPort().getPortName(), null));
				map.put("rxBufferByteCount", new CInt(serialDataReceivedEvent.getRXBufferByteCount(), null));
			} else {
				throw new EventException(
						"Cannot convert event to " + SerialDataAvailableEvent.class.getSimpleName() + ".");
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
			return new SimpleVersion(1, 0, 0);
		}
	}
	
	@api
	public static class serial_output_buffer_empty extends AbstractEvent {
		
		@Override
		public String getName() {
			return "serial_output_buffer_empty";
		}
		
		@Override
		public String docs() {
			return "{} Fired when the output buffer of a serial connection has become empty"
					+ " (i.e. all writing operations have finished)."
					+ " {string serialport: The serial port name} {}";
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
			if(event instanceof SerialOutputBufferEmptyEvent) {
				SerialOutputBufferEmptyEvent serialOutputBufferEmptyEvent = (SerialOutputBufferEmptyEvent) event;
				map.put("serialport", new CString(serialOutputBufferEmptyEvent.getSerialPort().getPortName(), null));
			} else {
				throw new EventException(
						"Cannot convert event to " + SerialOutputBufferEmptyEvent.class.getSimpleName() + ".");
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
			return new SimpleVersion(2, 0, 0);
		}
	}
}
