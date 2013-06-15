package it.xbladecopter.connection;

import it.xbladecopter.data.Protocol;
import it.xbladecopter.utils.BytesDecode;
import it.xbladecopter.utils.LogManager;
import it.xbladecopter.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

public class SerialDataRead implements Runnable {
	private LogManager logger = LogManager.getInstance();

	private boolean stop = false;
	private boolean radioInit = false;
	private InputStream iStream;

	public SerialDataRead() {
	}

	public synchronized boolean isStop() {
		return stop;
	}

	public synchronized void setStop(boolean stop) {
		this.stop = stop;
	}

	@Override
	public void run() {
		logger.logInfo("Start serial read thread", this.getClass());

		Protocol p = new Protocol();

		while (!isStop()) {

			try {
				while (iStream.available() > 0) {

					final byte[] bytes = new byte[iStream.available()];
					iStream.read(bytes);

					if (isRadioInit())
						logger.logInfo(
								"ACOM Reply " + BytesDecode.getHex(bytes),
								this.getClass());
					else
						for (int i = 0; i < bytes.length; i++) {
							p.byteReceived((byte) bytes[i]);
							if (p.isDataReady())
								p.getData();
						}
				}

			} catch (IOException e) {
				logger.logError(e.getMessage(), this.getClass());
			}

			Utils.sleep(5);
		}

		if (iStream != null) {
			try {
				iStream.close();
				iStream = null;
			} catch (IOException e) {
				logger.logError(e.getMessage(), this.getClass());
			}
		}
	}

	public void setInputStream(InputStream in) {
		this.iStream = in;
	}

	public synchronized boolean isRadioInit() {
		return radioInit;
	}

	public synchronized void setRadioInit(boolean radioInit) {
		this.radioInit = radioInit;
	}

}
