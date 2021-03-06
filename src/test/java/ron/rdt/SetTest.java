package ron.rdt;

import org.junit.Assert;
import org.junit.Test;
import ron.Batch;
import ron.Frame;
import ron.Parse;
import ron.Slice;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static ron.FrameAppend.FORMAT_FRAME_LINES;
import static ron.FrameAppend.FORMAT_HEADER_SPACE;
import static ron.FrameAppend.FRAME_FORMAT_CARPET;
import static ron.RdtTest.runRONTest;
import static ron.rdt.Set.makeSetReducer;

public class SetTest {

	@Test
	public void TestSet_Reduce() {
		String[][] tests = new String[][]{
			{
				"*set#test1@1=1",
						"*set#test1@2=2",
						"*set#test1@2:d!:0=2@1=1",
			},
			{
				"*set#test1@1!@=1",
						"*set#test1@2:1;",
						"*set#test1@2!:1,",
			},
			{
				"*set#test1@3:1;",
						"*set#test1@4:2;",
						"*set#test1@4:d!:2,@3:1,",
			},
			{
				"*set#test1@2!@=2@1=1",
						"*set#test1@5!@=5@3:2,@4:1,",
						"*set#test1@5!@=5@3:2,@4:1,",
			},
			{
				"*set#test1@2!@=2@1=1",
						"*set#test1@3!@:2,@4:1,",
						"*set#test1@5!@=5",
						"*set#test1@5!@=5@3:2,@4:1,",
			},
			{
				"*set#test1@3!@:2,@4:1,",
						"*set#test1@5!@=5",
						"*set#test1@2!@=2@1=1",
						"*set#test1@2!@5=5@3:2,@4:1,",
			},
			{
				"*set#mice@1YKDY54a01+1YKDY5!>mouse$1YKDY5",
						"*set#mice@1YKDXO3201+1YKDXO?!@>mouse$1YKDXO@(WBF901(WBY>mouse$1YKDWBY@[67H01[6>mouse$1YKDW6@(Uh4j01(Uh>mouse$1YKDUh@(S67V01(S6>mouse$1YKDS6@(Of(N3:1YKDN3DS01+1YKDN3,@(MvBV01(IuJ:0>mouse$1YKDIuJ@(LF:1YKDIuEY01+1YKDIuJ,:{A601,@(Io5l01[oA:0>mouse$1YKDIoA@[l7_01[l>mouse$1YKDIl@(57(4B:1YKD4B3f01+1YKD4B,@(0bB401+1YKCsd:0>mouse$1YKCsd@1YKCu6+:1YKCsd7Q01+1YKCsd,",
						"*set#mice@1YKDXO3201+1YKDXO!@(Y54a01(Y5>mouse$1YKDY5@(XO3201(XO>mouse$1YKDXO@(WBF901(WBY>mouse$1YKDWBY@[67H01[6>mouse$1YKDW6@(Uh4j01(Uh>mouse$1YKDUh@(S67V01(S6>mouse$1YKDS6@(Of(N3:1YKDN3DS01+1YKDN3,@(MvBV01(IuJ:0>mouse$1YKDIuJ@(LF:1YKDIuEY01+1YKDIuJ,:{A601,@(Io5l01[oA:0>mouse$1YKDIoA@[l7_01[l>mouse$1YKDIl@(57(4B:1YKD4B3f01+1YKD4B,@(0bB401+1YKCsd:0>mouse$1YKCsd@1YKCu6+:1YKCsd7Q01+1YKCsd,",
			},
		};
		Reducer cs = makeSetReducer();
		for (int i = 0; i < tests.length; i++) {
			String[] test = tests[i];
			String[] inputs = Arrays.copyOfRange(test, 0, test.length -1);
			Batch batch = Parse.parseStringBatch(inputs);
			Frame result = cs.reduce(batch);
			if (!result.string().equals(test[test.length - 1])) {
				System.err.println("got  " + result.string());
				System.err.println("want " + test[test.length - 1]);
				Frame expectedNoFormat = Parse.parseFrameString(test[test.length - 1]);
//				Frame expectedFormated = Frame.makeFormattedFrame(FRAME_FORMAT_CARPET, 1024);
				Frame expectedFormated = expectedNoFormat.reformat(FRAME_FORMAT_CARPET);
//				for (;!expectedNoFormat.eof();) {
//					expectedFormated.append(expectedNoFormat);
//					expectedNoFormat.next();
//				}
				System.err.println(result.string().length() + " != " + test[test.length - 1].length());
				Assert.fail(String.format("%d set reduce fail, \ngot\n'%s' \nwant\n'%s'\n", i, result.reformat(FRAME_FORMAT_CARPET).string(), expectedFormated.string()));
			}
		}
	}

	@Test
	public void test() {
		Frame frame = Parse.parseFrameString("*set#mice@1YKDXO3201+1YKDXO?!@>mouse$1YKDXO@(WBF901(WBY>mouse$1YKDWBY@[67H01[6>mouse$1YKDW6@(Uh4j01(Uh>mouse$1YKDUh@(S67V01(S6>mouse$1YKDS6@(Of(N3:1YKDN3DS01+1YKDN3,@(MvBV01(IuJ:0>mouse$1YKDIuJ@(LF:1YKDIuEY01+1YKDIuJ,:{A601,@(Io5l01[oA:0>mouse$1YKDIoA@[l7_01[l>mouse$1YKDIl@(57(4B:1YKD4B3f01+1YKD4B,@(0bB401+1YKCsd:0>mouse$1YKCsd@1YKCu6+:1YKCsd7Q01+1YKCsd,");
		while (!frame.eof()) {
			System.out.println(Arrays.toString(frame.atoms));
			frame.next();
		}
	}

	@Test
	public void TestSet_Reduce_Basic() {
		runRONTest(
				makeSetReducer(),
				"01-set-basic.ron"
				);
	}
}
