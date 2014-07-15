package Test; /**
 * Created by saggezza on 7/15/14.
 */


@TesterInfo(
        createdBy = "Kevin Gleason",
        tags = { "sales", "test" }
)
public class TestExample {

    @Test
    void testA() {
        if (true)
            throw new RuntimeException("This test always failed");
    }

    @Test(enabled = false)
    void testB() {
        if (false)
            throw new RuntimeException("This test always passed");
    }

    @Test(enabled = true)
    void testC() {
        if (10 > 1) {
            // do nothing, this test always passed.
        }
    }

}