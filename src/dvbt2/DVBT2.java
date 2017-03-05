package dvbt2;

import java.nio.ByteOrder;
import java.nio.file.Paths;

import org.jscience.mathematics.number.Complex;

import com.jeffreybosboom.serviceproviderprocessor.ServiceProvider;

import channel.Channel;
import edu.mit.streamjit.api.Input;
import edu.mit.streamjit.api.Pipeline;
import edu.mit.streamjit.impl.compiler2.Compiler2StreamCompiler;
import edu.mit.streamjit.test.Benchmark;
import edu.mit.streamjit.test.Benchmarker;
import edu.mit.streamjit.test.SuppliedBenchmark;
import receiver.ReceiverTerminal.ReceiverKernel;
import transmitter.streamjit.TransmitterTerminal.TransmitterKernel;

/**
 * @author Sumanaruban Rajadurai (Suman)
 *
 */
public class DVBT2 {

	public static void main(String[] args) {
		System.out.println("========================== DVB T2 START =============================");
		Compiler2StreamCompiler sc = new Compiler2StreamCompiler();
		sc.maxNumCores(4);
		sc.multiplier(1);
		Benchmarker.runBenchmark(new DVBT2Benchmark(), sc).get(0).print(System.out);

	}

	@ServiceProvider(Benchmark.class)
	public static final class DVBT2Benchmark extends SuppliedBenchmark {
		public DVBT2Benchmark() {
			super("DVBT2", DVBT2Kernel.class, new Dataset("/data/bus_cif.yuv", (Input) Input
					.fromBinaryFile(Paths.get("./data/bus_cif.yuv"), Byte.class, ByteOrder.LITTLE_ENDIAN)));
		}
	}

	public static final class DVBT2Kernel extends Pipeline<Byte, Complex> {
		public DVBT2Kernel() {
			this.add(new TransmitterKernel(), new Channel(), new ReceiverKernel());
		}
	}
}
