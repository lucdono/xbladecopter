package it.xbladecopter.connection;

import it.xbladecopter.Activator;
import it.xbladecopter.utils.LogManager;
import it.xbladecopter.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;

public class SerialDataWrite implements Runnable {
	private LogManager logger = LogManager.getInstance();

	private boolean stop = false;
	private OutputStream oStream;

	public synchronized boolean isStop() {
		return stop;
	}

	public synchronized void setStop(boolean stop) {
		this.stop = stop;
	}

	public void setStream(OutputStream out) {
		this.oStream = out;
	}

	public void closeStream() {
		if (oStream != null)
			try {
				oStream.close();
			} catch (IOException e) {
				logger.logError(e.getMessage(), this.getClass());
			}
	}

	@Override
	public void run() {
		byte buffer[] = new byte[80];
		int i = 0;

		IOConsole debugConsole = Activator.getDefault().getDebugConsole();
		IOConsoleInputStream stream = debugConsole.getInputStream();
		logger.logInfo("Start serial write thread", this.getClass());

		while (!isStop()) {

			try {
				while (stream.available() > 0) {
					int c = stream.read();
					buffer[i++] = (byte) c;
					if (c == 0x0d) {
						oStream.write(buffer, 0, i);
						System.err.println(Arrays.toString(buffer));
					}
					if (c == 0x0a)
						i = 0;
					if (i == 80)
						i = 0;
				}
			} catch (Exception e1) {
			}

			Utils.sleep(10);
		}

		if (stream != null)
			try {
				stream.close();
				stream = null;
			} catch (IOException e) {
				logger.logError(e.getMessage(), this.getClass());
			}

	}

	public void setOutputStream(OutputStream outputStream) {
		this.oStream = outputStream;
	}

	public void initRadio() throws IOException, InterruptedException {

		byte ENTER_COMMAND_MODE[] = { 0x41, 0x54, 0x2B, 0x2B, 0x2B, 0x0D };
		byte EXIT_COMMAND_MODE[] = { (byte) 0xCC, 0x41, 0x54, 0x4F, 0x0D };
		byte SET_SERVER[] = { (byte) 0xCC, 0x03, 0x00 };
		byte DESTINATION_ADDRESS[] = { (byte) 0xCC, 0x10, 0x28, 0x72, 0x39 };

		oStream.write(ENTER_COMMAND_MODE);
		oStream.flush();
		TimeUnit.MILLISECONDS.sleep(100);

		oStream.write(SET_SERVER);
		oStream.flush();
		TimeUnit.MILLISECONDS.sleep(100);

		oStream.write(DESTINATION_ADDRESS);
		oStream.flush();
		TimeUnit.MILLISECONDS.sleep(100);

		oStream.write(EXIT_COMMAND_MODE);
		oStream.flush();
		TimeUnit.MILLISECONDS.sleep(100);

		TimeUnit.MILLISECONDS.sleep(500);
	}

}
