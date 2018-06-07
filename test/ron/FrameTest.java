package ron;

import org.junit.Assert;
import org.junit.Test;
import ron.Frame;
import ron.Parse;

import static ron.Const.TERM_HEADER;
import static ron.UUID.COMMENT_UUID;

public class FrameTest {

	@Test
	public void TestBatchFrames() {

		String frame1 = "*lww#A@1!:a=1:b=2:c=3";
		String frame2 = "*lww#A@2!:d=4";
		Batch batch = new Batch();
		batch = batch.append(Parse.parseFrameString(frame1));
		batch = batch.append(Parse.parseFrameString(frame2));
		Frame frame12 = batch.join();
		String batchStr = "*lww#A@1!:a=1:b=2:c=3*#@2:0!:d=4";
		if (!frame12.string().equals(batchStr)) {
			System.err.printf("\n%s != \n%s\n", frame12.string(), batchStr);
			throw new RuntimeException("Fail");
		}
//		b2 := frame12.Split()
//		if len(b2) != 2 {
//			t.Fail()
//			t.Log("length", len(b2))
//			return
//		}
//		if b2[0].String() != frame1 {
//			t.Fail()
//			t.Logf("%s != %s\n", b2[0].String(), frame1)
//		}
//		if b2[1].String() != frame2 {
//			t.Fail()
//			t.Logf("%s != %s\n", b2[0].String(), frame1)
//		}
	}

	@Test
	public void TestFrame_Split() {
		Frame frame = Parse.parseFrameString("*lww#id1!:val=1*#id2:0!:val=2");
		System.out.println(frame.Parser);

		String h1 = "*lww#id1!:val=1";
		String h2 = "*lww#id2!:val=2";
		frame.next();
		frame.next();
		if (frame.term() != TERM_HEADER) {
			throw new RuntimeException("Fail");
		}
		final Pair<Frame, Frame> pair = frame.split2();
		Frame id1 = pair.getLeft();
		Frame id2 = pair.getRight();
		if (!id1.string().equals(h1)) {
			System.err.printf("\nneed: %s\nhave: %s\n", h1, id1.string());
			throw new RuntimeException("Fail left");
		}
		if (!id2.string().equals(h2)) {
			System.err.printf("\nneed: %s\nhave: %s\n", h2, id2.string());
			throw new RuntimeException("Fail right");
		}
		if (!id2.type().equals(UUID.newName("lww"))) {
			throw new RuntimeException("Fail");
		}
	}

	@Test
	public void TestFrame_SplitMultiframe() {
		String multiStr = "*lww#test!:a=1*#best:0!:b=2:c=3*#:d=4;";
		String[] splits = new String[] {
			"*lww#test!:a=1",
					"*lww#best!:b=2:c=3",
					"*lww#best:d=4",
		};
		Frame multi = Parse.parseFrameString(multiStr);

		Batch monos = multi.split();
		for (int i = 0; i < monos.frames.length; i++) {
			if (!monos.frames[i].string().equals(splits[i])) {
				Assert.fail(String.format("split fail at %d:\n'%s'\nshould be\n'%s'\n", i, monos.frames[i].string(), splits[i]));
			}
		}
	}

	@Test
	public void TestBatch_Equal() {
		Batch b1 = Parse.parseStringBatch(new String[] {"*one", "*two"});
		Batch b2 = Parse.parseStringBatch(new String[] {"*one*two"});
		boolean eq = b1.equals(b2);
		if (!eq) {
			Assert.fail();
		}
		b2 = b2.append(Parse.parseFrameString("*three"));
		eq = b1.equals(b2);
		if (eq) {
			Assert.fail();
		}
	}

	@Test
	public void estFrame_Copy() {
		Frame a = Parse.parseFrameString("*~'comment' *lww#obj!");
		Frame b = a;
		if (!b.type().equals(COMMENT_UUID)) {
			Assert.fail("improper copy");
		}
		b.next();
		if (!a.type().equals(COMMENT_UUID)) {
			Assert.fail("the copy is still linked");
		}
	}

	@Test
	public void TestFrame_Split2() {
		Frame frame = Parse.parseFrameString("*rga#test@4!@1'A'@2'B'*#@4:rm!:3,");
		Batch split = frame.split();
		boolean eq = split.equals(new Batch(frame));
		if (!eq) {
			Assert.fail(String.format("split fail, \n%s\nbecame\n%s", frame.string(), split.string()));
		}
	}

	@Test
	public void TestFrame_Empties() {
		Frame head = Parse.parseFrameString("*lww #obj @)4+UserAlice ?\n* # @ !\n");
		Frame next = Parse.parseFrameString("*lww #obj @)4+UserAlice ?\n* # @ !\n");
		next.next();
		if (!head.type().equals(next.type()) || !head.object().equals(next.object()) || !head.event().equals(next.event()) || !head.ref().equals(next.ref())) {
			Assert.fail("failed default");
		}
		if (next.term() != TERM_HEADER) {
			Assert.fail("incorrect term");
		}
	}


}
