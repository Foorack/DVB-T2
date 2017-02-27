package receiver;

import edu.mit.streamjit.api.Filter;
import edu.mit.streamjit.api.Pipeline;

public class BitsToBytes extends Pipeline<Byte, Byte> {

	public BitsToBytes() {
		this.add(new MakeBytes());
	}

	private static class MakeBytes extends Filter<Byte, Byte> {

		public MakeBytes() {
			super(8, 1);
		}

		@Override
		public void work() {
			byte[] data_array = new byte[8];
			StringBuilder builder = new StringBuilder();
			byte byte_value;

			for (int i = 0; i < 8; i++) {
				data_array[i] = pop();
				builder.append(data_array[i]);
			}
			byte_value = (byte) Integer.parseInt(builder.toString(), 2);
			// System.out.println(builder.toString()+" "+byte_value);
			// WriteToFile writer = new WriteToFile();
			// writer.appendToFile(byte_value, path);
			// System.out.println(byte_value+" ");
			push(byte_value);

		}
	}
}
