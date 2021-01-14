package io.github.pieter12345.msserialconnection.exceptions.CRE;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.typeof;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CClassType;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREException;

import jssc.SerialPort;

@SuppressWarnings("serial")
@typeof("msserialconnection.SerialPortException")
public class CRESerialPortException extends CREException {
	
	public static final CClassType TYPE = CClassType.get(CRESerialPortException.class);
	
	private final SerialPort serialPort;
	
	public CRESerialPortException(SerialPort serialPort, String msg, Target t) {
		super(msg, t);
		this.serialPort = serialPort;
	}
	
	public CRESerialPortException(SerialPort serialPort, String msg, Target t, Throwable cause) {
		super(msg, t, cause);
		this.serialPort = serialPort;
	}
	
	@Override
	public CArray getExceptionObject() {
		CArray ret = super.getExceptionObject();
		ret.set("serialport", this.serialPort.getPortName());
		return ret;
	}
	
	@Override
	public String docs() {
		return "Thrown when setup of or interaction with a serial port fails.";
	}
	
	@Override
	public Version since() {
		return MSVersion.V3_3_4;
	}
}
