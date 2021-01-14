package io.github.pieter12345.msserialconnection;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.ArgumentValidation;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CByteArray;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.events.Driver;
import com.laytonsmith.core.events.EventUtils;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;

import io.github.pieter12345.msserialconnection.exceptions.CRE.CREIllegalStateException;
import io.github.pieter12345.msserialconnection.exceptions.CRE.CRESerialPortException;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * Container class for serial connection MethodScript functions.
 * @author P.J.S. Kools
 */
// By convention, classes are named after the MS functions that they represent.
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:HideUtilityClassConstructor"})
public class SerialConnectionFunctions {
	
	public static String docs() {
		return "Provides serial connection API that can be used to communicate with serial devices through"
				+ " COMPORT/USB.";
	}
	
	@api
	public static class serial_get_ports extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{0};
		}
		
		@Override
		public String docs() {
			return "array {} Returns an array containing all serial ports in the system.";
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {};
		}
		
		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			CArray ret = new CArray(t);
			for(String portName : SerialPortList.getPortNames()) {
				ret.push(new CString(portName, t), t);
			}
			return ret;
		}
	}
	
	@api
	public static class serial_connect extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{7};
		}
		
		@Override
		public String docs() {
			return "void {string portName, int baudRate, int numDataBits, int numStopBits, string parity,"
					+ " boolean setRTS, boolean setDTR} Opens a serial connection to the given serial port using the"
					+ " given parameters."
					+ " parity has to be one of: PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK and PARITY_SPACE."
					+ " Many common microprocessors work with settings: numDataBits = 8, numStopBits = 1,"
					+ " parity = PARITY_NONE, setRTS = true and setDTR = true. Look up the specifications of your"
					+ " serial device if this does not work."
					+ " Throws IllegalArgumentException when parity is not one of: PARITY_NONE, PARITY_ODD,"
					+ " PARITY_EVEN, PARITY_MARK and PARITY_SPACE."
					+ " Throws IllegalStateException when the given serial port has already been opened by "
					+ MSSerialConnection.class.getSimpleName() + "."
					+ " Throws SerialPortException when connecting failed.";
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {
					CREIllegalArgumentException.class, CREIllegalStateException.class, CRESerialPortException.class};
		}
		
		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			
			// Get arguments.
			String portName = ArgumentValidation.getStringObject(args[0], t);
			int baudRate = ArgumentValidation.getInt32(args[1], t);
			int numDataBits = ArgumentValidation.getInt32(args[2], t);
			int numStopBits = ArgumentValidation.getInt32(args[3], t);
			String parityStr = ArgumentValidation.getStringObject(args[4], t);
			boolean setRTS = ArgumentValidation.getBooleanObject(args[5], t);
			boolean setDTR = ArgumentValidation.getBooleanObject(args[6], t);
			
			// Validate parity argument.
			int parity;
			switch(parityStr.toUpperCase()) {
				case "PARITY_NONE": {
					parity = SerialPort.PARITY_NONE;
					break;
				}
				case "PARITY_ODD": {
					parity = SerialPort.PARITY_ODD;
					break;
				}
				case "PARITY_EVEN": {
					parity = SerialPort.PARITY_EVEN;
					break;
				}
				case "PARITY_MARK": {
					parity = SerialPort.PARITY_MARK;
					break;
				}
				case "PARITY_SPACE": {
					parity = SerialPort.PARITY_SPACE;
					break;
				}
				default: {
					throw new CREIllegalArgumentException("Parity has to be one of:"
							+ " PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK and PARITY_SPACE.", t);
				}
			}
			
			// Return IllegalStateException if the serial port is already connected to using MSSerialConnection.
			if(MSSerialConnection.SERIAL_CONNECTIONS.containsKey(portName)) {
				throw new CREIllegalStateException(
						"Serial port already opened by " + MSSerialConnection.class.getSimpleName() + ".", t);
			}
			
			// Create and open serial port.
			SerialPort serialPort = new SerialPort(portName);
			try {
				serialPort.openPort();
			} catch (SerialPortException e) {
				throw new CRESerialPortException(serialPort, "Failed to open port: " + e.getExceptionType(), t);
			}
			
			// Set serial port properties.
			try {
				if(!serialPort.setParams(baudRate, numDataBits, numStopBits, parity, setRTS, setDTR)) {
					throw new CRESerialPortException(serialPort, "Failed to set serial port connection parameters.", t);
				}
			} catch (SerialPortException e) {
				throw new Error(e); // Never thrown since the serial port was opened and never closed.
			}
			
			// Add serial port listener.
			try {
				serialPort.addEventListener((SerialPortEvent serialPortEvent) -> {
					try {
						
						// Read the data.
						byte[] data = serialPort.readBytes();
						
						// Fire the serial_data_received event when data is received.
						if(data != null) {
							EventUtils.TriggerListener(Driver.EXTENSION,
									"serial_data_received", new SerialDataReceivedEvent(serialPort, data));
						}
					} catch (SerialPortException e) {
						// Ignore. Disconnections are not handled by this event listener.
					}
				});
			} catch (SerialPortException e) {
				throw new Error(e); // Never thrown.
			}
			
			// Store the opened serial port reference.
			MSSerialConnection.SERIAL_CONNECTIONS.put(serialPort.getPortName(), serialPort);
			
			// Return void.
			return CVoid.VOID;
		}
	}
	
	@api
	public static class serial_is_connected extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}
		
		@Override
		public String docs() {
			return "boolean {string portName} Returns true if this port was opened by "
					+ serial_connect.class.getSimpleName() + "(), and not yet closed by "
					+ serial_disconnect.class.getSimpleName() + "()."
					+ " Note that connection failures are not automatically detected, and therefore not considered by"
					+ " this function.";
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {};
		}
		
		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			String portName = ArgumentValidation.getStringObject(args[0], t);
			return CBoolean.GenerateCBoolean(MSSerialConnection.SERIAL_CONNECTIONS.containsKey(portName), t);
		}
	}
	
	@api
	public static class serial_disconnect extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}
		
		@Override
		public String docs() {
			return "void {string portName} Disconnects from the given serial port."
					+ " Throws an IllegalStateException when the given serial port was not open.";
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalStateException.class};
		}
		
		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			
			// Get arguments.
			String portName = ArgumentValidation.getStringObject(args[0], t);
			
			// Throw CREIllegalStateException when the serial port was not opened.
			SerialPort serialPort = MSSerialConnection.SERIAL_CONNECTIONS.remove(portName);
			if(serialPort == null) {
				throw new CREIllegalStateException("Serial port was not opened.", t);
			}
			try {
				serialPort.closePort(); // Ignore result, if it can't be closed, then it probably is already.
			} catch (SerialPortException e) {
				// Ignore result, if it can't be closed, then it probably is already.
			}
			return CVoid.VOID;
		}
	}
	
	@api
	public static class serial_send extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{2};
		}
		
		@Override
		public String docs() {
			return "void {string serialPort, byte_array data} Sends the given data to the given serial connection."
					+ " Throws IllegalStateException when the given serial port was not open."
					+ " Throws SerialPortException when sending the data to the serial port fails.";
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalStateException.class, CRESerialPortException.class};
		}
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			
			// Get arguments.
			String portName = ArgumentValidation.getStringObject(args[0], t);
			CByteArray data = ArgumentValidation.getByteArray(args[1], t);
			
			// Throw CREIllegalStateException when the serial port was not opened.
			SerialPort serialPort = MSSerialConnection.SERIAL_CONNECTIONS.get(portName);
			if(serialPort == null) {
				throw new CREIllegalStateException("Serial port was not opened.", t);
			}
			
			// Write the bytes to the serial connection.
			try {
				if(!serialPort.writeBytes(data.asByteArrayCopy())) {
					throw new CRESerialPortException(serialPort, "Failed to send data to serial port.", t);
				}
			} catch (SerialPortException e) {
				throw new Error(e); // Never thrown since the serial port was opened and never closed.
			}
			
			// Return void.
			return CVoid.VOID;
		}
	}
	
	public abstract static class SerialConnectionFunction extends AbstractFunction {
		
		@Override
		public String getName() {
			return this.getClass().getSimpleName();
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
			return MSVersion.V3_3_4;
		}
	}
}
