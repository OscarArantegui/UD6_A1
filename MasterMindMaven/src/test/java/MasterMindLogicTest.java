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
    void testCheckGuess_AllCorrect() throws NoSuchFieldException, IllegalAccessException {
        // 1. Preparamos el secreto fijo (RGBY)
        Color[] fixedSecret = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

        // 2. Inyectamos el secreto
        Field secretField = MasterMindLogic.class.getDeclaredField("SECRET");
        secretField.setAccessible(true);
        // podemos usar directamente el objeto 'masterMindLogic' del setUp
        secretField.set(masterMindLogic, fixedSecret);

        // 3. El intento es IDÉNTICO al secreto
        Color[] guess = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

        // 4. Ejecutamos la lógica
        MasterMindLogic.Result result = masterMindLogic.checkGuess(guess);

        // 5. Comprobamos: Todo son aciertos exactos (negros)
        assertEquals(4, result.blacks, "Debe haber 4 casillas negras (victoria)");
        assertEquals(0, result.whites, "No debe haber casillas blancas porque todas son negras");
    }
    @Test
    void testCheckGuess_AllWrong() throws NoSuchFieldException, IllegalAccessException {
        // 1. Preparamos el secreto fijo (RGBY)
        Color[] fixedSecret = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

        // 2. Inyectamos el secreto
        Field secretField = MasterMindLogic.class.getDeclaredField("SECRET");
        secretField.setAccessible(true);
        secretField.set(masterMindLogic, fixedSecret);

        // 3. El intento usa colores que NO están en el secreto
        Color[] guess = {Color.BLACK, Color.WHITE, Color.MAGENTA, Color.ORANGE};

        // 4. Ejecutamos la lógica
        MasterMindLogic.Result result = masterMindLogic.checkGuess(guess);

        // 5. Comprobamos: Ningún acierto
        assertEquals(0, result.blacks, "No debe haber casillas negras");
        assertEquals(0, result.whites, "No debe haber casillas blancas");
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
