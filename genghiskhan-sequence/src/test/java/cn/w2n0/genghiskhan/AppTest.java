package cn.w2n0.genghiskhan;

import static org.junit.Assert.assertTrue;

import cn.w2n0.genghiskhan.sequence.snowflake.SnowFlakeGenerate;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {

        System.out.println(SnowFlakeGenerate.getInstance().gen());
        assertTrue( true );
    }
}
