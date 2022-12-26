package io.github.pieter12345.msserialconnection;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.ArgumentValidation;
import com.laytonsmith.core.compiler.signature.FunctionSignatures;
import com.laytonsmith.core.compiler.signature.SignatureBuilder;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CByteArray;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.events.Driver;
import com.laytonsmith.core.events.EventUtils;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CRERangeException;
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
		
		public FunctionSignatures getFunctionSignatures() {
			return new SignatureBuilder(CArray.TYPE, "An array containing all serial ports in the system.").build();
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
					+ " Throws SerialPortException when the serial connection could not be set up.";
		}
		
		public FunctionSignatures getFunctionSignatures() {
			return new SignatureBuilder(CVoid.TYPE)
					.param(CString.TYPE, "portName", "The serial port identifier.")
					.param(CInt.TYPE, "baudRate", "The serial connection baud rate.")
					.param(CInt.TYPE, "numDataBits",
							"The number of data bits. Binary data is typically transmitted as eight bits.")
					.param(CInt.TYPE, "numStopBits", "The number of stop bits.")
					.param(CString.TYPE, "parity",
							"One of: PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK and PARITY_SPACE.")
					.param(CBoolean.TYPE, "setRTS", "Send Request To Send on connect.")
					.param(CBoolean.TYPE, "setDTR", "Send Data Terminal Ready on connect.")
					.throwsEx(CREIllegalArgumentException.class, "when parity has an invalid value.")
					.throwsEx(CREIllegalStateException.class, "when the given serial port is not open.")
					.throwsEx(CRESerialPortException.class, "when the serial connection could not be set up.")
					.build();
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
					switch(serialPortEvent.getEventType()) {
						case SerialPort.MASK_RXCHAR: {
							int rxBufferByteCount = serialPortEvent.getEventValue();
							EventUtils.TriggerListener(Driver.EXTENSION, "serial_data_available",
									new SerialDataAvailableEvent(serialPort, rxBufferByteCount));
							break;
						}
						case SerialPort.MASK_TXEMPTY: {
							EventUtils.TriggerListener(Driver.EXTENSION, "serial_output_buffer_empty",
									new SerialOutputBufferEmptyEvent(serialPort));
							break;
						}
						default: {
							// Ignore.
						}
					}
				}, SerialPort.MASK_RXCHAR | SerialPort.MASK_TXEMPTY);
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
		
		public FunctionSignatures getFunctionSignatures() {
			return new SignatureBuilder(CBoolean.TYPE, "True if the given serial port is open.").build();
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
			return "void {string portName} Disconnects the given serial port."
					+ " Throws IllegalStateException when the given serial port is not open.";
		}
		
		public FunctionSignatures getFunctionSignatures() {
			return new SignatureBuilder(CVoid.TYPE)
					.param(CString.TYPE, "portName", "The serial port identifier.")
					.throwsEx(CREIllegalStateException.class, "when the given serial port is not open.")
					.build();
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
			
			// Throw CREIllegalStateException when the serial port is not open.
			SerialPort serialPort = MSSerialConnection.SERIAL_CONNECTIONS.remove(portName);
			if(serialPort == null) {
				throw new CREIllegalStateException("Serial port is not open.", t);
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
	public static class serial_input_buffer_byte_count extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}
		
		@Override
		public String docs() {
			return "int {string portName} Returns the number of bytes in the RX (input) buffer.";
		}
		
		public FunctionSignatures getFunctionSignatures() {
			return new SignatureBuilder(CInt.TYPE, "The number of bytes in the RX (input) buffer.")
			.param(CString.TYPE, "portName", "The serial port identifier.")
			.throwsEx(CREIllegalStateException.class, "when the given serial port is not open.")
			.build();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {};
		}
		
		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			
			// Get arguments.
			String portName = ArgumentValidation.getStringObject(args[0], t);
			
			// Throw CREIllegalStateException when the serial port is not open.
			SerialPort serialPort = MSSerialConnection.SERIAL_CONNECTIONS.get(portName);
			if(serialPort == null) {
				throw new CREIllegalStateException("Serial port is not open.", t);
			}
			
			// Get and return the result.
			try {
				return new CInt(serialPort.getInputBufferBytesCount(), t);
			} catch (SerialPortException e) {
				throw new Error(); // Never thrown.
			}
		}
	}
	
	@api
	public static class serial_output_buffer_byte_count extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}
		
		@Override
		public String docs() {
			return "int {string portName} Returns the number of bytes in the TX (output) buffer.";
		}
		
		public FunctionSignatures getFunctionSignatures() {
			return new SignatureBuilder(CInt.TYPE, "The number of bytes in the TX (output) buffer.")
			.param(CString.TYPE, "portName", "The serial port identifier.")
			.throwsEx(CREIllegalStateException.class, "when the given serial port is not open.")
			.build();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {};
		}
		
		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			
			// Get arguments.
			String portName = ArgumentValidation.getStringObject(args[0], t);
			
			// Throw CREIllegalStateException when the serial port is not open.
			SerialPort serialPort = MSSerialConnection.SERIAL_CONNECTIONS.get(portName);
			if(serialPort == null) {
				throw new CREIllegalStateException("Serial port is not open.", t);
			}
			
			// Get and return the result.
			try {
				return new CInt(serialPort.getOutputBufferBytesCount(), t);
			} catch (SerialPortException e) {
				throw new Error(); // Never thrown.
			}
		}
	}
	
	@api
	public static class serial_write_bytes extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{2, 3, 4};
		}
		
		@Override
		public String docs() {
			return "void {string serialPort, byte_array data, [int startInd], [int length]}"
					+ " Writes data to the given serial connection."
					+ " When 'startInd' is given, writes bytes from the given data array starting at this index."
					+ " When 'length' is given, writes this amount of bytes from the given data array starting at"
					+ " the start index."
					+ " Throws IllegalStateException when the given serial port is not open."
					+ " Throws RangeException when startInd and/or startInd + length is out of array bounds."
					+ " Throws SerialPortException when writing the data to the serial port fails.";
		}
		
		@Override
		public FunctionSignatures getSignatures() {
			return new SignatureBuilder(CVoid.TYPE)
					.param(CString.TYPE, "serialPort", "The serial port identifier.")
					.param(CByteArray.TYPE, "data", "The data array to write from.")
					.param(CInt.TYPE, "startInd",
							"The start index in the data array from which to start writing.", true)
					.param(CInt.TYPE, "length", "The amount of bytes from the data array to write.", true)
					.throwsEx(CREIllegalStateException.class, "when the given serial port is not open.")
					.throwsEx(CRERangeException.class, "when startInd and/or startInd + length is out of array bounds.")
					.throwsEx(CRESerialPortException.class, "when writing the data to the serial port fails.")
					.build();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {CREIllegalStateException.class, CRERangeException.class, CRESerialPortException.class};
		}
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			
			// Get arguments.
			String portName = ArgumentValidation.getStringObject(args[0], t);
			CByteArray data = ArgumentValidation.getByteArray(args[1], t);
			int startInd;
			if(args.length >= 3) {
				startInd = ArgumentValidation.getInt32(args[2], t);
				if(startInd < 0 || startInd > data.size()) {
					throw new CRERangeException("Start index out of bounds in " + this.getName() + ": " + startInd, t);
				}
			} else {
				startInd = 0;
			}
			int length;
			if(args.length >= 4) {
				length = ArgumentValidation.getInt32(args[3], t);
				if(length < 0) {
					throw new CRERangeException("Invalid length in " + this.getName() + ": " + length, t);
				}
				if(startInd + length > data.size()) {
					throw new CRERangeException(
							"End index out of bounds in " + this.getName() + ": " + (startInd + length), t);
				}
			} else {
				length = (int) data.size() - startInd;
			}
			
			// Get subarray if necessary.
			if(startInd != 0 || length != data.size()) {
				data = data.getBytes(startInd, length);
			}
			
			// Throw CREIllegalStateException when the serial port is not open.
			SerialPort serialPort = MSSerialConnection.SERIAL_CONNECTIONS.get(portName);
			if(serialPort == null) {
				throw new CREIllegalStateException("Serial port is not open.", t);
			}
			
			// Write the bytes to the serial connection.
			try {
				if(!serialPort.writeBytes(data.asByteArrayCopy())) {
					throw new CRESerialPortException(serialPort, "Failed to send data to serial port.", t);
				}
			} catch (SerialPortException e) {
				throw new Error(e); // Never thrown since the serial port is open.
			}
			
			// Return void.
			return CVoid.VOID;
		}
	}
	
	@api
	public static class serial_read_bytes extends SerialConnectionFunction {
		
		@Override
		public Integer[] numArgs() {
			return new Integer[]{2, 3};
		}
		
		@Override
		public String docs() {
			return "byte_array {string serialPort, int length, [int timeoutMs]}"
					+ " Reads from the given serial connection."
					+ " Returns the read data when 'length' bytes have been read"
					+ " or when no data has been read for 'timeoutMs' milliseconds (-1 indicates no timeout)."
					+ " Throws IllegalArgumentException when length < 0 or timeoutMs < -1."
					+ " Throws IllegalStateException when the given serial port is not open."
					+ " Throws SerialPortException when reading from the serial port fails.";
		}
		
		@Override
		public FunctionSignatures getSignatures() {
			return new SignatureBuilder(CByteArray.TYPE, "The read data.")
					.param(CString.TYPE, "serialPort", "The serial port identifier.")
					.param(CInt.TYPE, "length", "The maximum amount of data to read.")
					.param(CInt.TYPE, "timeoutMs", "The maximum amount of milliseconds to wait between reads"
							+ " before deciding that no more data is available."
							+ " -1 indicates no timeout. Defaults to -1.", true)
					.throwsEx(CREIllegalArgumentException.class, "when length < 0 or timeoutMs < -1.")
					.throwsEx(CREIllegalStateException.class, "when the given serial port is not open.")
					.throwsEx(CRESerialPortException.class, "when writing the data to the serial port fails.")
					.build();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {
					CREIllegalArgumentException.class, CREIllegalStateException.class, CRESerialPortException.class};
		}
		
		@Override
		public Mixed exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			
			// Get arguments.
			String portName = ArgumentValidation.getStringObject(args[0], t);
			int length = ArgumentValidation.getInt32(args[1], t);
			int timeoutMs = (args.length >= 3 ? ArgumentValidation.getInt32(args[2], t) : -1);
			
			// Validate length and timeoutMs arguments.
			if(length < 0) {
				throw new CREIllegalArgumentException(
						"Length has to be >= 0 in " + this.getName() + ". Received: " + length, t);
			}
			if(timeoutMs < -1) {
				throw new CREIllegalArgumentException(
						"Timeout has to be >= -1 in " + this.getName() + ". Received: " + timeoutMs, t);
			}
			
			// Throw CREIllegalStateException when the serial port is not open.
			SerialPort serialPort = MSSerialConnection.SERIAL_CONNECTIONS.get(portName);
			if(serialPort == null) {
				throw new CREIllegalStateException("Serial port is not open.", t);
			}
			
			// Read the bytes from the serial connection.
			try {
				
				// Wait until enough data is available or the timeout has been exceeded.
				int numBytesAvailable;
				int lastNumBytesAvailable = 0;
				long lastReceiveTime = System.currentTimeMillis();
				do {
					numBytesAvailable = serialPort.getInputBufferBytesCount();
					if(numBytesAvailable >= length) {
						break; // Enough data available.
					} else if(numBytesAvailable != lastNumBytesAvailable) {
						lastNumBytesAvailable = numBytesAvailable;
						lastReceiveTime = System.currentTimeMillis();
					}
				} while((System.currentTimeMillis() - lastReceiveTime) < timeoutMs);
				
				// Read and return the data.
				byte[] readBytes = serialPort.readBytes((numBytesAvailable >= length ? length : numBytesAvailable));
				return CByteArray.wrap(readBytes, t);
			} catch (SerialPortException e) {
				throw new Error(e); // Never thrown since the serial port is open.
			}
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
			return new SimpleVersion(1, 0, 0);
		}
	}
}
