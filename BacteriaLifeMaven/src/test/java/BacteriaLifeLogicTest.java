import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class BacteriaLifeLogicTest {

    private BacteriaLifeLogic logic;
    private final int SIZE = 5;

    @BeforeEach
    void setUp() {
        // Inicializamos con un tamaño manejable
        logic = new BacteriaLifeLogic(SIZE);
    }

    @Test
    void testGenerateInitialGen_DimensionsAndValues() {
        int[][] initial = logic.generateInitialGen();

        // Verificamos dimensiones
        assertEquals(SIZE, initial.length);
        assertEquals(SIZE, initial[0].length);

        // Verificamos que solo contenga 0s y 1s
        for (int[] row : initial) {
            for (int cell : row) {
                assertTrue(cell == 0 || cell == 1, "La célula debe ser 0 o 1");
            }
        }
    }

    @Test
    void testCheckNeighbours_Center() {
        // Creamos un tablero manual 3x3
        // La del centro (1,1) tiene 3 vecinos arriba + 0 lados + 0 abajo
        int[][] grid = {
                {1, 1, 1},
                {0, 1, 0},
                {0, 0, 0}
        };

        // checkNeighbours es estático, podemos llamarlo directamente o via instancia
        int neighbours = BacteriaLifeLogic.checkNeighbours(grid, 1, 1);
        assertEquals(3, neighbours, "La celda central debería tener 3 vecinos");
    }

    @Test
    void testCheckNeighbours_Corner() {
        // Probamos una esquina para asegurar que no da error de índice (OutOfBounds)
        int[][] grid = {
                {0, 1},
                {1, 1}
        };

        // Esquina superior izquierda (0,0). Vecinos: Derecha(1), Abajo(1), Diagonal(1)
        int neighbours = BacteriaLifeLogic.checkNeighbours(grid, 0, 0);
        assertEquals(3, neighbours, "La esquina (0,0) debería tener 3 vecinos");
    }

    @Test
    void testRule_DeathByIsolation() {
        // Célula viva en el centro, sin vecinos
        int[][] currentGen = {
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
        };

        int[][] nextGen = logic.generateNewGen(currentGen);

        // La célula central (1,1) debe morir (0)
        assertEquals(0, nextGen[1][1], "La bacteria debería morir por soledad (0 vecinos)");
    }

    @Test
    void testRule_DeathByOvercrowding() {
        // Célula central rodeada de 4 vecinos (cruz)
        int[][] currentGen = {
                {0, 1, 0},
                {1, 1, 1},
                {0, 1, 0}
        };

        int[][] nextGen = logic.generateNewGen(currentGen);

        // La célula central (1,1) debe morir porque tiene 4 vecinos
        assertEquals(0, nextGen[1][1], "La bacteria debería morir por sobrepoblación (4 vecinos)");
    }

    @Test
    void testRule_Birth() {
        // Célula muerta en el centro rodeada de 3 vivas
        int[][] currentGen = {
                {1, 1, 0},
                {1, 0, 0},
                {0, 0, 0}
        };

        int[][] nextGen = logic.generateNewGen(currentGen);

        // La célula central (1,1) debe nacer (1)
        assertEquals(1, nextGen[1][1], "Debería nacer una bacteria (3 vecinos)");
    }

    @Test
    void testRule_Survival() {
        // Célula viva con 2 vecinos
        int[][] currentGen = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        int[][] nextGen = logic.generateNewGen(currentGen);

        assertEquals(1, nextGen[1][1], "La bacteria debería sobrevivir (2 vecinos)");
    }

    @Test
    void testPattern_Blinker() {
        // Estado A: Línea horizontal
        int[][] horizontal = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0}, // Fila central viva
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        // Estado B esperado: Línea vertical
        int[][] verticalExpected = {
                {0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 1, 0, 0}, // Columna central viva
                {0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0}
        };

        // Generamos siguiente generación
        int[][] result = logic.generateNewGen(horizontal);

        // Usamos deepEquals para comparar arrays multidimensionales
        assertTrue(Arrays.deepEquals(verticalExpected, result),
                "El patrón horizontal debería convertirse en vertical");

        // Si volvemos a generar, debería volver a horizontal
        int[][] resultBack = logic.generateNewGen(result);
        assertTrue(Arrays.deepEquals(horizontal, resultBack),
                "El patrón vertical debería volver a horizontal");
    }

    @Test
    void testCheckStableGen() {
        int[][] genA = {{0, 1}, {1, 0}};
        int[][] genB = {{0, 1}, {1, 0}}; // Idéntico
        int[][] genC = {{0, 0}, {0, 0}}; // Distinto

        assertTrue(BacteriaLifeLogic.checkStableGen(genA, genB), "Debería detectar estabilidad (iguales)");
        assertFalse(BacteriaLifeLogic.checkStableGen(genA, genC), "Debería detectar inestabilidad (distintos)");
    }
}