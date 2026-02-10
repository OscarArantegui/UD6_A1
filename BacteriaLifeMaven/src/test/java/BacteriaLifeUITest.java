import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BacteriaLifeUITest {

    private BacteriaLifeUI ui;
    private BacteriaLifeLogic mockLogic;
    private final int DIMENSION = 30; // Debe coincidir con la constante de UI

    @BeforeEach
    void setUp() {
        mockLogic = Mockito.mock(BacteriaLifeLogic.class);

        // Configuramos el mock para que devuelva un tablero inicial válido de 30x30
        int[][] initialBoard = new int[DIMENSION][DIMENSION];
        // Llenamos la primera casilla para tener algo que testear
        initialBoard[0][0] = 1;

        when(mockLogic.generateInitialGen()).thenReturn(initialBoard);
        when(mockLogic.getRound()).thenReturn(0);

        // Inicializamos UI
        ui = new BacteriaLifeUI(mockLogic);
    }

    @AfterEach
    void tearDown() {
        if (ui.getFrame() != null) {
            ui.getFrame().dispose();
        }
    }

    @Test
    void testInitialization_Structure() {
        assertNotNull(ui.getFrame());
        assertTrue(ui.getFrame().isVisible());
        assertNotNull(ui.getGenPanel());
        assertNotNull(ui.getStartButton());

        // Verificamos que se llamó a la lógica para crear el tablero inicial
        verify(mockLogic, times(1)).generateInitialGen();
    }

    @Test
    void testGridCreation_CorrectNumberOfCells() {
        JPanel genPanel = ui.getGenPanel();

        // El panel debe tener DIMENSION * DIMENSION componentes (30*30 = 900)
        assertEquals(900, genPanel.getComponentCount(), "El grid debe tener 900 células");

        // Verificamos que el primer componente es un Círculo y es NEGRO (porque initialBoard[0][0] = 1)
        Component c = genPanel.getComponent(0);
        assertTrue(c instanceof BacteriaLifeUI.Circle);
        BacteriaLifeUI.Circle circle = (BacteriaLifeUI.Circle) c;

        // Nota: En tu UI, 1 = BLACK, 0 = WHITE
        assertEquals(Color.BLACK, circle.getColor(), "La célula viva debe ser negra");
    }

    @Test
    void testStartButton_Exists() {
        JButton btn = ui.getStartButton();
        assertEquals("Start", btn.getText());
        assertTrue(btn.getActionListeners().length > 0, "El botón Start debe tener listeners");
    }

    @Test
    void testEvolutionStep_UpdatesUI() {
        // PREPARAR
        // Simulamos que la lógica devuelve un nuevo tablero en el paso 1
        int[][] nextBoard = new int[DIMENSION][DIMENSION];
        // En el nuevo tablero, la posición 0,0 muere (0) y la 0,1 nace (1)
        nextBoard[0][1] = 1;

        when(mockLogic.generateNewGen(any())).thenReturn(nextBoard);
        when(mockLogic.getRound()).thenReturn(1);

        // ACTUAR
        // Llamamos manualmente al método que ejecutaría el Timer
        ui.performEvolutionStep();

        // VERIFICAR
        // 1. La lógica fue llamada
        verify(mockLogic, times(1)).generateNewGen(any());

        // 2. El array interno de la UI se actualizó
        assertArrayEquals(nextBoard, ui.getBacteriaGen());

        // 3. La etiqueta de ronda se actualizó
        assertEquals("Round: 1", ui.getRoundLabel().getText());

        // 4. Verificamos visualmente un cambio en el grid (Componente 1 ahora debe ser negro)
        JPanel genPanel = ui.getGenPanel();
        BacteriaLifeUI.Circle circleNewAlive = (BacteriaLifeUI.Circle) genPanel.getComponent(1); // fila 0, col 1
        assertEquals(Color.BLACK, circleNewAlive.getColor(), "La nueva célula viva debe pintarse de negro");
    }
}