import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MasterMindUITest {
    private MasterMindUI masterMindUI;
    private MasterMindLogic mockLogic;

    // Datos de prueba
    private final Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    private final String[] labels = {"R", "G", "B", "Y"};
    private final int rounds = 10;

    @BeforeEach
    void setUp() {
        // 1. Mock de la lógica
        mockLogic = Mockito.mock(MasterMindLogic.class);

        // 2. Inicializamos la UI (El JFrame se crea dentro)
        masterMindUI = new MasterMindUI(colors, labels, rounds, mockLogic);
    }

    @AfterEach
    void tearDown() {
        if (masterMindUI.getFrame() != null) {
            masterMindUI.getFrame().dispose();
        }
    }
    @Test
    void testInit_CreatesCorrectStructure() {
        // Verificación directa gracias al getter
        assertEquals(rounds, masterMindUI.getGuessRows().size());
        assertTrue(masterMindUI.getFrame().isVisible());
        assertNotNull(masterMindUI.getCheckButton());
    }
    @Test
    void testSelectColor_UpdatesState() {
        // Configuramos estado directamente
        masterMindUI.setSelectedColor(Color.RED);

        assertEquals(Color.RED, masterMindUI.getSelectedColor());
    }
    @Test
    void testPaintCircle_WithSelectedColor() {
        // Seleccionar color
        masterMindUI.setSelectedColor(Color.BLUE);

        // Obtener círculo
        // Como Circle ya no es private, podemos usarlo
        MasterMindUI.Circle firstCircle = masterMindUI.getGuessRows().get(0)[0];

        // Simular clic
        firstCircle.doClick();

        // Verificar
        assertEquals(Color.BLUE, firstCircle.getColor());
    }
    @Test
    void testCheckButton_IncompleteRow_ShowsWarning() {
        // Usamos mockStatic para JOptionPane
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // Clic en Check sin pintar nada
            masterMindUI.getCheckButton().doClick();

            pane.verify(() -> JOptionPane.showMessageDialog(null, "Please fill all slots before checking!"));
            verify(mockLogic, never()).checkGuess(any());
        }
    }
    @Test
    void testCheckButton_CompleteRow_CallsLogicAndShowsResult() {
        // Pintar toda la fila
        masterMindUI.setSelectedColor(Color.RED);
        MasterMindUI.Circle[] currentRow = masterMindUI.getGuessRows().get(0);

        for (MasterMindUI.Circle c : currentRow) {
            c.doClick(); // Simular pintar cada círculo
        }

        // Configurar el Mock
        MasterMindLogic.Result resultDummy = new MasterMindLogic.Result(1, 1); //Uno bien y uno mal
        when(mockLogic.checkGuess(any())).thenReturn(resultDummy);

        // Ejecutar Check
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            masterMindUI.getCheckButton().doClick();

            // Verificar llamada a la lógica
            verify(mockLogic, times(1)).checkGuess(any());

            // Verificar feedback
            pane.verify(() -> JOptionPane.showMessageDialog(null, "Black: 1. White: 1"));
        }
    }
    @Test
    void testWinCondition_ShowsVictoryMessage() {
        // Pintar fila
        masterMindUI.setSelectedColor(Color.GREEN);
        for (MasterMindUI.Circle c : masterMindUI.getGuessRows().get(0)) {
            c.doClick();
        }

        // Mockear VICTORIA
        when(mockLogic.checkGuess(any())).thenReturn(new MasterMindLogic.Result(4, 0));

        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            masterMindUI.getCheckButton().doClick();
            pane.verify(() -> JOptionPane.showMessageDialog(null, "You guessed it!"));
        }
    }

    @Test
    void testLoseCondition_ShowsLossMessage() {
        // orzar última ronda
        masterMindUI.setCurrentRow(9);

        // Pintar la última fila
        masterMindUI.setSelectedColor(Color.YELLOW);
        for (MasterMindUI.Circle c : masterMindUI.getGuessRows().get(9)) {
            c.doClick();
        }

        // Mockear fallo y secreto
        when(mockLogic.checkGuess(any())).thenReturn(new MasterMindLogic.Result(0, 0));
        when(mockLogic.showSecret()).thenReturn("RGBY");

        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            masterMindUI.getCheckButton().doClick();

            // Verificar que se revela el secreto
            pane.verify(() -> JOptionPane.showMessageDialog(null, "You lost, the answer was: RGBY"));
        }
    }
}
