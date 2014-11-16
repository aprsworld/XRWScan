import java.io.IOException;

import com.focus_sw.fieldtalk.BusProtocolException;
import com.focus_sw.fieldtalk.MbusRtuMasterProtocol;


public class XRWScan {
	MbusRtuMasterProtocol mbus;
	
	protected double anemometer40R(int pulseTime) {
		if ( 0==pulseTime || 65535==pulseTime )
			return 0.0;
		
		return (0.857*10000.0)/pulseTime + 0.725;
		
		
	}
	
	protected double anemometer40HC(int pulseTime) {
		if ( 0==pulseTime || 65535==pulseTime )
			return 0.0;
		
		return (1.711*10000.0)/pulseTime + 0.78;
		
		
	}

	
	protected boolean getCounters(int networkAddress) {
		short s[] = new short[18];
		
		try { 
			mbus.readInputRegisters(networkAddress, 0, s);
			
			System.out.println("A" + anemometer40HC(s[1]));
//			System.out.println("B" + anemometer40R(s[7]));
//			System.out.println("C" + anemometer40R(s[13]));
			
			
		} catch ( BusProtocolException bpe ) {
			System.err.println("# BusProtocolException: " + bpe);
			return true;
		} catch ( IOException ioe ) {
			System.err.println("# IOException: " + ioe);
			return false;
		} catch ( Exception e ) {
			System.err.println("# Exception: " + e);
			return false;
		}
		
		return true;
	}
	
	public void run(String serialPort, int serialSpeed, int networkAddress) throws Exception {
		
		/* open modbus connection to host */
		mbus = new MbusRtuMasterProtocol();
		mbus.configureCountFromZero();

		mbus.setRetryCnt(0);
		mbus.setTimeout(1000);
		
		mbus.openProtocol(serialPort, serialSpeed);
		
		
		System.err.println("# Opening Modbus RTU connection");
//		mbus.openProtocol();
		
		
		System.err.println("# Starting scan");
		System.err.println("# Network Address: " + networkAddress);


		while ( true ) {
			if ( ! getCounters(networkAddress) ) {
				break;
			}
		}
		
		
		
		if ( mbus.isOpen() ) {
			System.err.println("# Closing Modbus RTU connection");
			mbus.closeProtocol();
		}
			
		
	}
	
	
	public static void main(String[] args) {
		System.err.println("# XRWScan 2014-11-15 (precision)");
		
		if ( args.length < 2 ) {
			System.err.println("usage: XRWScan hostname part");
			System.exit(1);
		}
		
		String serialPort=args[0];
		int serialSpeed = Integer.parseInt(args[1]);
		
		int networkAddress=24;

		
		if ( args.length >= 3 ) {
			networkAddress = Integer.parseInt(args[2]);
		}
		
		try { 
			new XRWScan().run(serialPort, serialSpeed, networkAddress);
		} catch ( Exception e ) {
			System.err.println("# Exception while scanning: ");
			System.err.println(e.toString());
			e.printStackTrace();
		}

	}

}
