package io.github.pieter12345.msserialconnection;

import java.util.Map;
import java.util.TreeMap;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CByteArray;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.events.AbstractEvent;
import com.laytonsmith.core.events.BindableEvent;
import com.laytonsmith.core.events.Driver;
import com.laytonsmith.core.exceptions.EventException;
import com.laytonsmith.core.exceptions.PrefilterNonMatchException;

public class Events {
	
	@api
	public static class serial_data_received extends AbstractEvent {
		
		@Override
		public String getName() {
			return "serial_data_received";
		}
		
		@Override
		public String docs() {
			return "Fired when input data from an serial connection is received."
					+ " Format: {data: The raw received data, message: The received message} ";
		}
		
		@Override
		public BindableEvent convert(CArray arg0, Target arg1) {
			return null;
		}
		
		@Override
		public Driver driver() {
			return Driver.EXTENSION;
		}
		
		@Override
		public Map<String, Construct> evaluate(BindableEvent event) throws EventException {
			Map<String, Construct> map = new TreeMap<>();
			if(event instanceof SerialDataEvent) {
				map.put("data", CByteArray.wrap(((SerialDataEvent) event).getData(), null));
				map.put("message", new CString(((SerialDataEvent) event).getMessage(), null));
			} else {
				throw new EventException("Cannot convert event to " + SerialDataEvent.class.getSimpleName() + ".");
			}
			return map;
		}
		
		@Override
		public boolean matches(Map<String, Construct> arg0, BindableEvent arg1) throws PrefilterNonMatchException {
			return true;
		}
		
		@Override
		public boolean modifyEvent(String arg0, Construct arg1, BindableEvent arg2) {
			return false;
		}
		
		@Override
		public Version since() {
			return CHVersion.V3_3_1;
		}
    }
}
