# MSSerialConnection
Provides serial connection API for the MethodScript programming language.

# Functions
## SerialConnectionFunctions
Provides serial connection API that can be used to communicate with serial devices through COMPORT/USB.

### void serial\_connect(string portName, int baudRate, int numDataBits, int numStopBits, string parity, boolean setRTS, boolean setDTR):
Opens a serial connection to the given serial port using the given parameters. parity has to be one of: PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK and PARITY_SPACE. Many common microprocessors work with settings: numDataBits = 8, numStopBits = 1, parity = PARITY_NONE, setRTS = true and setDTR = true. Look up the specifications of your serial device if this does not work. Throws IllegalArgumentException when parity is not one of: PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK and PARITY_SPACE. Throws IllegalStateException when the given serial port has already been opened by MSSerialConnection. Throws SerialPortException when the serial connection could not be set up.

### void serial\_disconnect(string portName):
Disconnects the given serial port. Throws IllegalStateException when the given serial port is not open.

### array serial\_get\_ports():
Returns an array containing all serial ports in the system. Returns null when serial port data could not be obtained due to a recently disconnected device.

### int serial\_input\_buffer\_byte\_count(string portName):
Returns the number of bytes in the RX (input) buffer.

### boolean serial\_is\_connected(string portName):
Returns true if this port was opened by serial_connect(), and not yet closed by serial_disconnect(). Note that connection failures are not automatically detected, and therefore not considered by this function.

### int serial\_output\_buffer\_byte\_count(string portName):
Returns the number of bytes in the TX (output) buffer.

### byte\_array serial\_read\_bytes(string serialPort, int length, [int timeoutMs]):
Reads from the given serial connection. Returns the read data when 'length' bytes have been read or when no data has been read for 'timeoutMs' milliseconds (-1 indicates no timeout). Throws IllegalArgumentException when length < 0 or timeoutMs < -1. Throws IllegalStateException when the given serial port is not open. Throws SerialPortException when reading from the serial port fails.

### void serial\_write\_bytes(string serialPort, byte\_array data, [int startInd], [int length]):
Writes data to the given serial connection. When 'startInd' is given, writes bytes from the given data array starting at this index. When 'length' is given, writes this amount of bytes from the given data array starting at the start index. Throws IllegalStateException when the given serial port is not open. Throws RangeException when startInd and/or startInd + length is out of array bounds. Throws SerialPortException when writing the data to the serial port fails.

# Events
## SerialConnectionEvents
Contains events related to serial connections.

### serial\_data\_available
Fired when data is received through a serial connection.
#### Prefilters

#### Event Data
**string serialport**: The serial port name  
**byte_array data**: The raw received data
#### Mutable Fields

### serial\_output\_buffer\_empty
Fired when the output buffer of a serial connection has become empty (i.e. all writing operations have finished).
#### Prefilters

#### Event Data
**string serialport**: The serial port name
#### Mutable Fields
