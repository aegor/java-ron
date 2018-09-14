package ron;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

public class FrameHeapTest {

	@Test
	public void TestIHeap_AddFrame() {
		String frameA = "*lww#test@time1-orig:number=1,@(2:string'2'";
		String frameB = "*lww#test@time3-orig:number=3,@(4:string'4'";
		String frameC = "*lww#test@time1-orig:number=1,@(2:string'2'@(3:number=3@(4:string'4'";
		IHeap heap = makeFrameHeap(FrameHeap.eventComparator(), null, 2);
		heap.putFrame(Parse.parseFrameString(frameA));
		heap.putFrame(Parse.parseFrameString(frameB));
		Frame C = heap.frame();
		if (!C.string().equals(frameC)) {
			Assert.fail(String.format("heap fail: \n'%s' must be \n'%s'", C.string(), frameC));
		}
	}

	@Test
	public void TestIHeap_Op() {
		String[] frames = new String[] {
			"*lww#test@time1-orig:number=1@(2:string'2'",
					"*lww#test@time3-orig:number=3@(4:string'4'",
					"*lww#test@time2-orig:number=2@(2:string'2'@(3:number=3@(4:string'4'",
		};
		IHeap heap = makeFrameHeap(FrameHeap.refComparator(), null, 2);
		heap.putAll(Parse.parseStringBatch(frames));
		UUID loc = heap.current().ref();
		int count = 0;
		for (; heap.current().ref().equals(loc);) {
			System.out.println(heap.current().string());
			count++;
			heap.next();
		}
		if (count != 3) {
			Assert.fail(String.format("%d counts", count));
		}
	}

	@Test
	public void TestIHeap_Merge() {
		String frameA = "*rga#test!@1:0'A'@2'B'"; //  D E A C B
		String frameB = "*rga#test!@1:0'A'@3'C'";
		String frameC = "*rga#test!@4:0'D'@5'E'";
		String frameR = "*rga#test@4'D',@5'E'@1'A'@3'C'@2'B'";
		IHeap heap = makeFrameHeap(FrameHeap.eventComparatorDesc(), FrameHeap.refComparator(), 4);
		heap.putFrame(Parse.parseFrameString(frameA));
		heap.putFrame(Parse.parseFrameString(frameB));
		heap.putFrame(Parse.parseFrameString(frameC));
		Frame res = Frame.makeFrame(128);
		for (;!heap.eof();) {
			res.append(heap.current());
			heap.nextPrim();
		}
		if (!res.string().equals(frameR)) {
			Assert.fail(String.format("merge fail: \n'%s' must be \n'%s'", res.string(), frameR));
		}
	}

	private IHeap makeFrameHeap(Comparator<Frame> primary, Comparator<Frame> secondary, int size) {
//		return FrameHeap.makeFrameHeap(primary, secondary, size);
		return FrameArrayHeap.makeFrameHeap(primary, secondary, size);
	}

}
