package receiver.streamblocks;

import java.nio.ByteOrder;
import java.nio.file.Paths;

import org.jscience.mathematics.number.Complex;

import com.jeffreybosboom.serviceproviderprocessor.ServiceProvider;

import edu.mit.streamjit.api.Filter;
import edu.mit.streamjit.api.Input;
import edu.mit.streamjit.api.Pipeline;
import edu.mit.streamjit.impl.compiler2.Compiler2StreamCompiler;
import edu.mit.streamjit.test.Benchmark;
import edu.mit.streamjit.test.Benchmarker;
import edu.mit.streamjit.test.SuppliedBenchmark;

/*
 * source: http://algs4.cs.princeton.edu/99scientific/FFT.java
 */
public class FFT_block {
	public static void main(String[] args) throws InterruptedException {
		//compile2streamcompiler
		Compiler2StreamCompiler sc = new Compiler2StreamCompiler();
		sc.maxNumCores(4);
		sc.multiplier(4);
		Benchmarker.runBenchmark(new TransmitterBenchmark(), sc).get(0).print(System.out);
	}
	
	@ServiceProvider(Benchmark.class)
	public static final class TransmitterBenchmark extends SuppliedBenchmark {
		public TransmitterBenchmark() {
			super("Transmitter", TransmitterKernel.class, new Dataset("receiver_data.in",
					(Input)Input.fromBinaryFile(Paths.get("receiver_data.in"), Byte.class, ByteOrder.LITTLE_ENDIAN)
//					, (Supplier)Suppliers.ofInstance((Input)Input.fromBinaryFile(Paths.get("/home/jbosboom/streamit/streams/apps/benchmarks/asplos06/fft/streamit/FFT5.out"), Float.class, ByteOrder.LITTLE_ENDIAN))
			));
		}
	}
	
	public static final class TransmitterKernel extends Pipeline<Byte, Complex> {
		
		public TransmitterKernel() {
			// filter blocks are executer according to this order
//			this.add(new Add5(),new AddPair(),new Add5(),  new Printer());
			this.add(new ConvertToComplex(), new dofft());
		}		
	}
	/*public static void main(String[] args) {
		 	int N = 8;
		 	 Complex[] x = new Complex[N];
	        // original data
		 	for (int i = 0; i < N; i++) {
	            x[i] = Complex.valueOf(i, 0);
	            x[i] = Complex.valueOf(-2*Math.random() + 1, 0);	        
	        }
	        show(x, "x");
	        
	     // FFT of original data
	        Complex[] y = fft(x);
	        show(y, "y = fft(x)");
      
	}*/
	private static class ConvertToComplex extends Filter<Byte, Complex> {
		
		public ConvertToComplex() {
			super(2, 1);
		}

		@Override
		public void work() {
			Byte a = pop();
			Byte b = pop();
			Complex out = Complex.valueOf(a, b);
			System.out.print(out+"\t");
			push(out);
		}
	}
	
	private static class dofft extends Filter<Complex, Complex> {
		
		public dofft() {
			super(16, 16);
		}

		@Override
		public void work() {
			System.out.println("\ndo fft work method------------------------");
			Complex[] before_fft = new Complex[16];
			for (int i = 0; i < before_fft.length; i++) {
				before_fft[i] = pop();
			}
			
			Complex[] after_fft = new Complex[16];
			after_fft = fft(before_fft);
			
			for (int i = 0; i < after_fft.length; i++) {
				push(after_fft[i]);
				System.out.print(after_fft[i]+"\t");
			}
			System.out.println();
		}
	}
	
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) {
            throw new IllegalArgumentException("N is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = Complex.valueOf(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
    
    // display an array of Complex numbers to standard output
    private static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
        	System.out.println(x[i]);
        }
        System.out.println();
    }

}