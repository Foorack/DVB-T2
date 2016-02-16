package transmitter.streamjit;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.jscience.mathematics.number.Complex;

import edu.mit.streamjit.api.Filter;
import transmitter.FEC_Frame;

public class DataWriter extends edu.mit.streamjit.api.Pipeline<Complex, Complex>{
	
	public DataWriter(){
		this.add(new WriteToFile());
	}
	
	private static class WriteToFile extends edu.mit.streamjit.api.Filter<Complex, Complex> {
		
		public WriteToFile() {
			super(64, 64);
		}

		@Override
		public void work() {
			System.out.println("write to file----------------------");
			Complex[] data_array = new Complex[64];
			StringBuilder builder = new StringBuilder();
			Complex temp;
			for (int i = 0; i < data_array.length; i++) {
				temp = pop();
				data_array[i] = temp;
//				builder.append(temp.getReal()+" "+temp.getImaginary()+" ");
//				builder.append((int)temp.getReal()+" "+(int)temp.getImaginary()+" ");
				appendToFile2((byte)temp.getReal(), (byte)temp.getImaginary());
			}
			System.out.println("write...."+ builder.toString());
//			appendToFile2(builder.toString());
		}
		
		private static void appendToFile(String text){
			try(
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("receiver_data.in", true)))) {
					System.out.println("appending....");
				    out.println(text);
			}catch (IOException e) {
				    e.printStackTrace();
			}
		}
		
		private static void appendToFile2(byte real, byte img){
			
			DataOutputStream out=null;
//			DataInputStream in=null;
			try {
				out = new DataOutputStream(new FileOutputStream("video.yuv",true));
//				in = new DataInputStream(new FileInputStream("data.in"));				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}						
				
				try {
					out.write(real);
					out.write(img);
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			try {
//				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

	}

}
