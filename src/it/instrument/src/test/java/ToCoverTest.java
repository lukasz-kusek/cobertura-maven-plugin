import org.junit.Test;

public class ToCoverTest {

    @Test
    public void shouldRun1() {
        new ToCover1().run(0);
        new ToCover1().run(1);
    }

    @Test
    public void shouldRun2() {
        new ToCover2().run(1);
    }
}
