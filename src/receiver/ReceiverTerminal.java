package receiver;

import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jscience.mathematics.number.Complex;

import edu.mit.streamjit.api.CompiledStream;
import edu.mit.streamjit.api.Input;
import edu.mit.streamjit.api.OneToOneElement;
import edu.mit.streamjit.api.Output;
import edu.mit.streamjit.api.Pipeline;
import edu.mit.streamjit.api.StreamCompiler;
import edu.mit.streamjit.impl.compiler2.Compiler2StreamCompiler;
import edu.mit.streamjit.test.Benchmarker;
import edu.mit.streamjit.test.SuppliedBenchmark;

public class ReceiverTerminal {

	public static void main(String[] args) throws InterruptedException {
		StreamCompiler sc = new Compiler2StreamCompiler();
		OneToOneElement<Complex, Byte> streamGraph = new ReceiverKernel();
		Path path = Paths.get("data/dvbtransmitter.out");
		Input<Complex> input = Input.fromBinaryFile(path, Complex.class, ByteOrder.LITTLE_ENDIAN);
		CompiledStream cs = sc.compile(streamGraph, input, Output.blackHole());
		cs.awaitDrained();

		// Benchmarker.runBenchmark(new ReceiverBenchmark(), sc).get(0).print(System.out);
	}

	public static final class ReceiverBenchmark extends SuppliedBenchmark {

		public ReceiverBenchmark() {
			super("Receiver", ReceiverKernel.class, new Dataset("data/dvbtransmitter.out", Input
					.fromBinaryFile(Paths.get("data/dvbtransmitter.out"), Complex.class, ByteOrder.LITTLE_ENDIAN)));
		}
	}

	public static final class ReceiverKernel extends Pipeline<Complex, Byte> {

		public ReceiverKernel() {
			this.add(new FFT(), new CellDeinterleaver(), new Constellation_Derotation(), new De_Normalizer(),
					new Maximum_Likelyhood_Mapper(), new Constellation_De_mapper(), new Multiplexer(),
					new Column_De_Twister(), new Bit_DeInterleaver(), new DeScrambbler(), new BBHeaderRemovel(),
					new PushDataBits(), new BitsToBytes(), new ByteToSource());
		}
	}
}