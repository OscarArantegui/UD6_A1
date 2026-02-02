import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

public class MasterMindLogicTest {
    MasterMindLogic masterMindLogic;
    private Color[] palette;
    private String[] labels;
    private int secretLength = 4;

    @BeforeEach
    void setUp(){
        palette = new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        labels = new String[]{"R", "G", "B", "Y"};
        masterMindLogic = new MasterMindLogic(palette, secretLength, labels);
    }
    @Test
    void testMasterMindLogicConstructor(){
        var MasterMindLogic = new MasterMindLogic();
        assertNotNull(MasterMindLogic);
    }
    @Test
    void testGenerateSecret(){
        //Verificamos que al crear objeto hay secreto
        assertNotNull(masterMindLogic);
        //Ahora confirmamos que no es null o vacio
        String secretString = masterMindLogic.showSecret();
        assertNotNull(secretString);
        assertFalse(secretString.isEmpty());
    }
    //Usaremos spy para comprobar el intento
    @Test
    void testCheckGuess_spy() throws NoSuchFieldException, IllegalAccessException {
        MasterMindLogic spyLogic = spy(masterMindLogic);
        //Definimos colores secretos (RGBY)
        Color[] fixedSecret = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        //Inyectamos el secreto creado al objeto ya inicializado, el cual asigna valor aleatoriamente. Tenemos que cambiar el acceso al ser final y privado
        Field secretField = MasterMindLogic.class.getDeclaredField("SECRET");
        secretField.setAccessible(true);
        secretField.set(spyLogic, fixedSecret);
        //Inventarnos intento de usuario
        Color[] guess = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        MasterMindLogic.Result result = spyLogic.checkGuess(guess);
        //Comprobar con asserts cuantos negros (2) y cuantos blancos (2) hay
        assertEquals(2, result.blacks, "Debe haber 2 casillas negras");
        assertEquals(2, result.whites, "Debe haber 2 casillas blancas");
    }
    @Test
    void testResultConstructor() {
        int blacks = 2;
        int whites = 2;
        MasterMindLogic.Result result = new MasterMindLogic.Result(blacks, whites);
        assertEquals(blacks, result.blacks);
        assertEquals(whites, result.whites);
    }
    @Test
    void testShowSecret() throws NoSuchFieldException, IllegalAccessException {
        MasterMindLogic spyLogic = spy(masterMindLogic);
        //Definimos colores secretos (RGBY)
        Color[] fixedSecret = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        //Inyectamos el secreto creado al objeto ya inicializado, el cual asigna valor aleatoriamente. Tenemos que cambiar el acceso al ser final y privado
        Field secretField = MasterMindLogic.class.getDeclaredField("SECRET");
        secretField.setAccessible(true);
        secretField.set(spyLogic, fixedSecret);

        String secretString = spyLogic.showSecret();
        assertNotNull(secretString);
        assertFalse(secretString.isEmpty());
        assertEquals("RGBY", secretString);
    }
}
