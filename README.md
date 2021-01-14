# MSSerialConnection
Provides serial connection API for the MethodScript programming language.

# Functions
## SerialConnectionFunctions
Provides serial connection API that can be used to communicate with serial devices through COMPORT/USB.

### void serial\_connect(string portName, int baudRate, int numDataBits, int numStopBits, string parity, boolean setRTS, boolean setDTR):
Opens a serial connection to the given serial port using the given parameters. parity has to be one of: PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK and PARITY_SPACE. Many common microprocessors work with settings: numDataBits = 8, numStopBits = 1, parity = PARITY_NONE, setRTS = true and setDTR = true. Look up the specifications of your serial device if this does not work. Throws IllegalArgumentException when parity is not one of: PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK and PARITY_SPACE. Throws IllegalStateException when the given serial port has already been opened by MSSerialConnection. Throws SerialPortException when connecting failed.

### void serial\_disconnect(string portName):
Disconnects from the given serial port. Throws an IllegalStateException when the given serial port was not open.

### array serial\_get\_ports():
Returns an array containing all serial ports in the system.

### boolean serial\_is\_connected(string portName):
Returns true if this port was opened by serial_connect(), and not yet closed by serial_disconnect(). Note that connection failures are not automatically detected, and therefore not considered by this function.

### void serial\_send(string serialPort, byte\_array data):
Sends the given data to the given serial connection. Throws IllegalStateException when the given serial port was not open. Throws SerialPortException when sending the data to the serial port fails.

# Events
## SerialConnectionEvents
Contains events related to serial connections.

### serial\_data\_received
Fired when data is received through a serial connection.
#### Prefilters

#### Event Data
**string serialport**: The serial port name  
**byte_array data**: The raw received data
#### Mutable Fields
