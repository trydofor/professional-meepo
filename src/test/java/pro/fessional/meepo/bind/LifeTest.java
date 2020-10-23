package pro.fessional.meepo.bind;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-16
 */
public class LifeTest {

    @Test
    public void testPoint() {
        // 1,3,5-9
        Life l1 = Life.parse("1,5-7,6-8,5-8,7-9,6-9,5-9,7,7,8,3");
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(Const.TXT_EMPTY, l1.name);
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(1, l1.enjoy());
            Assert.assertEquals(Life.State.Skip, l1.state());
            Assert.assertEquals(2, l1.enjoy());
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(3, l1.enjoy());
            Assert.assertEquals(Life.State.Skip, l1.state());
            Assert.assertEquals(4, l1.enjoy());
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(5, l1.enjoy());
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(6, l1.enjoy());
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(7, l1.enjoy());
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(8, l1.enjoy());
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(9, l1.enjoy());
            Assert.assertEquals(Life.State.Dead, l1.state());
            Assert.assertEquals(10, l1.enjoy());
            l1.reset();
        }
    }

    @Test
    public void testOne() {
        Life l1 = Life.parse("");
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(Const.TXT_EMPTY, l1.name);
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(1, l1.enjoy());
            Assert.assertEquals(Life.State.Dead, l1.state());
            Assert.assertEquals(2, l1.enjoy());
            l1.reset();
        }

        Life l2 = Life.parse("1");
        Assert.assertEquals(Const.TXT_EMPTY, l2.name);
        Assert.assertEquals(Life.State.Live, l2.state());
        Assert.assertEquals(1, l2.enjoy());
        Assert.assertEquals(Life.State.Dead, l2.state());
        Assert.assertEquals(2, l2.enjoy());
    }

    @Test
    public void testAny() {
        Life l1 = Life.parse("*");
        Assert.assertEquals(Const.TXT_EMPTY, l1.name);
        for (int i = 1; i <= 256; i++) {
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(i, l1.enjoy());
        }
        l1.reset();
        for (int i = 1; i <= 256; i++) {
            Assert.assertEquals(Life.State.Live, l1.state());
            Assert.assertEquals(i, l1.enjoy());
        }

        Life l2 = Life.parse("id");
        Assert.assertEquals("id", l2.name);
        for (int i = 1; i <= 256; i++) {
            Assert.assertEquals(Life.State.Live, l2.state());
            Assert.assertEquals(i, l2.enjoy());
        }
    }

    @Test
    public void testOther() {
        Assert.assertEquals(Life.parse("1"), Life.parse("   1   "));
        Assert.assertEquals(Life.parse("1"), Life.parse("   1,   "));
        Assert.assertEquals(Life.parse("1,3"), Life.parse("   1,3   "));
        Assert.assertEquals(Life.parse("1,3"), Life.parse("   1,3,   "));
        Assert.assertEquals(Life.parse("1-3"), Life.parse("   1-3,   "));
    }
}