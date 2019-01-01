package pl.smartplayer.smartplayerapp;

import org.junit.Test;

import pl.smartplayer.smartplayerapp.field.Field;
import pl.smartplayer.smartplayerapp.main.MainActivity;
import pl.smartplayer.smartplayerapp.utils.Point2D;

import static org.junit.Assert.*;
import static pl.smartplayer.smartplayerapp.utils.UtilMethods.getPixelPositionByPoints;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getPixelPositionByPointsDebug(){
        Point2D leftDown = new Point2D(54.368236, 18.620061);
        Point2D leftUp = new Point2D(54.368526, 18.620979);
        Point2D rightUp = new Point2D(54.367693, 18.621735);
        Point2D player = new Point2D(54.367932, 18.620762);


        int x = getPixelPositionByPoints(leftUp,leftDown,player);
        int y = getPixelPositionByPoints(leftUp,rightUp,player);
    }
}