package io.github.pieter12345.msserialconnection.exceptions.CRE;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.typeof;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.constructs.CClassType;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREException;

@SuppressWarnings("serial")
@typeof("msserialconnection.IllegalArgumentException")
public class CREIllegalStateException extends CREException {
	
	public static final CClassType TYPE = CClassType.get(CREIllegalStateException.class);
	
	public CREIllegalStateException(String msg, Target t) {
		super(msg, t);
	}
	
	public CREIllegalStateException(String msg, Target t, Throwable cause) {
		super(msg, t, cause);
	}
	
	@Override
	public String docs() {
		return "Signals that a function has been called at an illegal or inappropriate time.";
	}
	
	@Override
	public Version since() {
		return MSVersion.V3_3_4;
	}
}
