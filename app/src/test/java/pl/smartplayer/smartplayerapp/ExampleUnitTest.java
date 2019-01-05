package pl.smartplayer.smartplayerapp;

import android.graphics.Point;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import pl.smartplayer.smartplayerapp.field.Field;
import pl.smartplayer.smartplayerapp.main.MainActivity;
import pl.smartplayer.smartplayerapp.utils.Point2D;

import static org.junit.Assert.*;
import static pl.smartplayer.smartplayerapp.utils.UtilMethods.getPixelPosition;
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
        final Point2D leftDown = new Point2D(54.368236, 18.620061);
        final Point2D leftUp = new Point2D(54.368526, 18.620979);
        final Point2D rightUp = new Point2D(54.367693, 18.621735);
        final Point2D rightDown = new Point2D(54.367401, 18.620810);

        Map<String,Map<String, Double>> coordinatesMap = new HashMap<>();
        coordinatesMap.put("leftUp",new HashMap<String, Double>(){{
            put("lat",leftUp.getX());
            put("lon",leftUp.getY());
        }});

        coordinatesMap.put("leftDown",new HashMap<String, Double>(){{
            put("lat",leftDown.getX());
            put("lon",leftDown.getY());
        }});
        coordinatesMap.put("rightUp",new HashMap<String, Double>(){{
            put("lat",rightUp.getX());
            put("lon",rightUp.getY());
        }});
        coordinatesMap.put("rightDown",new HashMap<String, Double>(){{
            put("lat",rightDown.getX());
            put("lon",rightDown.getY());
        }});
        MainActivity.sSelectedField = new Field("test","test",true,coordinatesMap,3);

        Point2D player = new Point2D(54.367697, 18.619942);

        Point x = getPixelPosition(player);
    }
}