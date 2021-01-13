package io.github.pieter12345.msserialconnection;

import java.io.IOException;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CByteArray;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CancelCommandException;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.exceptions.CRE.CREPluginInternalException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.functions.AbstractFunction;

import io.github.pieter12345.arduinoconnection.ArduinoConnectionException;
import io.github.pieter12345.arduinoconnection.UnsupportedException;

public class ArduinoFunctions {
	
	public static String docs() {
		return "Provides an api to communicate with serial devices through comport/USB.";
	}
	
	@api
	public static class serial_send extends AbstractFunction {
		
		@Override
		public String getName() {
			return "serial_send";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}
		
		@Override
		public String docs() {
			return "boolean {byte_array | string} Sends the given data to the serial connection."
					+ " Returns whether the data has been sent successfully or not.";
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREFormatException.class, CREPluginInternalException.class};
		}
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws CancelCommandException, ConfigRuntimeException {
			if(args.length != 1) {
				throw new CREFormatException("Wrong amount of arguments passed to " + this.getName(), t);
			}
			if(MSSerialConnection.arduinoConn == null) {
				throw new CREPluginInternalException(this.getName() + " requires an Arduino to be connected.", t);
			}
			Construct c = args[0];
			boolean success;
			if(c instanceof CByteArray) {
				success = MSSerialConnection.arduinoConn.send(((CByteArray) c).asByteArrayCopy());
			} else {
				success = MSSerialConnection.arduinoConn.send(c.val());
			}
			return CBoolean.GenerateCBoolean(success, t);
		}
		
		@Override
		public boolean isRestricted() {
			return false;
		}
		
		@Override
		public Boolean runAsync() {
			return true;
		}
		
		@Override
		public Version since() {
			return CHVersion.V3_3_1;
		}
	}
	
	@api
	public static class serial_reconnect extends AbstractFunction{
		
		@Override
		public String getName() {
			return "serial_reconnect";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{0};
		}
		
		@Override
		public String docs() {
			return "boolean {} Reconnects to the Arduino. Returns whether the reconnect was succesful or not.";
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {};
		}
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws CancelCommandException, ConfigRuntimeException {
			try {
				MSSerialConnection.reconnect();
				return CBoolean.TRUE;
			} catch (ArduinoConnectionException | UnsupportedException | IOException e) {
				return CBoolean.FALSE;
			}
		}
		
		@Override
		public boolean isRestricted() {
			return false;
		}
		
		@Override
		public Boolean runAsync() {
			return true;
		}
		
		@Override
		public Version since() {
			return CHVersion.V3_3_1;
		}
	}
	
	@api
	public static class serial_disconnect extends AbstractFunction{
		
		@Override
		public String getName() {
			return "serial_disconnect";
		}
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{0};
		}
		
		@Override
		public String docs() {
			return "void {} Disconnects from the Arduino, releasing the serial port.";
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {};
		}
		
		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws CancelCommandException, ConfigRuntimeException {
			MSSerialConnection.disconnect();
			return CVoid.VOID;
		}
		
		@Override
		public boolean isRestricted() {
			return false;
		}
		
		@Override
		public Boolean runAsync() {
			return true;
		}
		
		@Override
		public Version since() {
			return CHVersion.V3_3_1;
		}
	}
	
}
