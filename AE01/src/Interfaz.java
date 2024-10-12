import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Interfaz grafica.
 */
public class Interfaz extends JFrame {
    private JTextArea areaTexto;
    private InfoArchivos infoArchivos = new InfoArchivos();
    private JTextField campoBuscar;
    private JTextField campoReemplazar;
    private JCheckBox cajaSensibilidadMayusculas;
    private JCheckBox cajaSensibilidadAcentos;

    /**
     * Constructor de la clase Interfaz.
     */
    public Interfaz() {
        setTitle("Gestor de Archivos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        getContentPane().add(new JScrollPane(areaTexto), BorderLayout.CENTER);

        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new GridLayout(5, 2));
        panelEntrada.add(new JLabel("Buscar:"));
        campoBuscar = new JTextField();
        panelEntrada.add(campoBuscar);
        panelEntrada.add(new JLabel("Reemplazar con:"));
        campoReemplazar = new JTextField();
        panelEntrada.add(campoReemplazar);
        cajaSensibilidadMayusculas = new JCheckBox("Sensibilidad Mayúsculas");
        panelEntrada.add(cajaSensibilidadMayusculas);
        cajaSensibilidadAcentos = new JCheckBox("Sensibilidad Acentos");
        panelEntrada.add(cajaSensibilidadAcentos);
        getContentPane().add(panelEntrada, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout());
        JButton botonDir = new JButton("Seleccionar Directorio");
        botonDir.setBackground(new Color(255, 0, 0));
        botonDir.addActionListener(e -> seleccionarDirectorio());
        panelBotones.add(botonDir);
        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.addActionListener(e -> buscarEnArchivos());
        panelBotones.add(botonBuscar);
        JButton botonReemplazar = new JButton("Reemplazar");
        botonReemplazar.addActionListener(e -> reemplazarEnArchivos());
        panelBotones.add(botonReemplazar);
        getContentPane().add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Abre un dilogo para seleccionar un directorio.
     */
    private void seleccionarDirectorio() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int opcion = chooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File dirSeleccionado = chooser.getSelectedFile();
            listarArchivos(dirSeleccionado);
        }
    }

    /**
     * Lista los archivos del directorio seleccionado.
     * @param dir El directorio a listar.
     */
    private void listarArchivos(File dir) {
        areaTexto.setText("");
        String listaArchivos = infoArchivos.listarArchivos(dir);
        areaTexto.append(listaArchivos);
    }

    /**
     * Busca en los archivos del directorio seleccionado.
     */
    private void buscarEnArchivos() {
        String cadena = campoBuscar.getText();
        if (!cadena.isEmpty()) {
            boolean sensibilidadMayusculas = cajaSensibilidadMayusculas.isSelected();
            boolean sensibilidadAcentos = cajaSensibilidadAcentos.isSelected();
            String resultados = infoArchivos.buscarCoincidencias(new File("C:\\Users\\edvar\\Documents\\test"), cadena, sensibilidadMayusculas, sensibilidadAcentos);
            areaTexto.setText(resultados);
        } else {
            JOptionPane.showMessageDialog(this, "ERROR");
        }
    }

    /**
     * Reemplaza texto en los archivos del directorio seleccionado.
     */
    private void reemplazarEnArchivos() {
        String cadena = campoBuscar.getText();
        String nuevaCadena = campoReemplazar.getText();
        if (!cadena.isEmpty() && !nuevaCadena.isEmpty()) {
            boolean sensibilidadMayusculas = cajaSensibilidadMayusculas.isSelected();
            boolean sensibilidadAcentos = cajaSensibilidadAcentos.isSelected();
            String resultados = infoArchivos.reemplazarCoincidencias(new File("C:\\Users\\edvar\\Documents\\test"), cadena, nuevaCadena, sensibilidadMayusculas, sensibilidadAcentos);
            areaTexto.setText(resultados);
        } else {
            JOptionPane.showMessageDialog(this, "ERROR");
        }
    }

    /**
     * Metodo principal que inicia la aplicación.
     * @param args Argumentos de línea de comando.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Interfaz app = new Interfaz();
            app.setVisible(true);
        });
    }
}
