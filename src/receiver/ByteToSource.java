package receiver;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.mit.streamjit.api.Filter;
import edu.mit.streamjit.api.Pipeline;

public class ByteToSource extends Pipeline<Byte, Byte> {

	public ByteToSource() {
		this.add(new WriteToFile());
	}

	public static class WriteToFile extends Filter<Byte, Byte> {

		final DataOutputStream out;

		public WriteToFile() {
			super(20, 20);
			out = openOutFile();
		}

		private static DataOutputStream openOutFile() {
			DataOutputStream file = null;
			try {
				file = new DataOutputStream(new FileOutputStream("./out.yuv", true));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			return file;
		}

		@Override
		public void work() {
			// System.out.println("byte to source----------------------");
			// byte value = pop();
			// appendToFile(value);
			///////////////////////////////////////
			byte[] data = new byte[20];
			for (int i = 0; i < data.length; i++) {
				data[i] = pop();
				push(data[i]);
			}
			byteToSource(data, out);
			/////////////////////////////////////////
			// push(value);

		}

		public static void byteToSource(byte[] data, DataOutputStream out) {
			System.out.println("data length =" + data.length);
			int size = data.length;

			for (int i = 0; i < size; i++) {
				byte l = data[i];

				try {
					out.write(l);
					// System.out.print(l+" ");
					// byte val=in.readByte();
					// System.out.println(val);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Done..");
		}

		public static void appendToFile(byte value) {

			DataOutputStream out = null;
			// DataInputStream in=null;
			try {
				out = new DataOutputStream(new FileOutputStream("./out.yuv", true));
				// in = new DataInputStream(new FileInputStream("data.in"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				out.write(value);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				// in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void finalize() {
			if (out == null)
				return;
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
