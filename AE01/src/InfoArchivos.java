import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Esta clase proporciona metodos para obtener información sobre archivos y directorios,
 * asa como para realizar operaciones de busqueda y reemplazo en archivos de texto.
 * 
 * @author Edvard Jakialis
 * @version 1.0
 */
public class InfoArchivos {

    /**
     * Lista recursivamente los archivos en un directorio y sus subdirectorios.
     * 
     * @param dir El directorio a listar.
     * @return Una cadena con la información de los archivos encontrados.
     */
    public String listarArchivos(File dir) {
        StringBuilder listaArchivos = new StringBuilder();
        File[] archivos = dir.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    listaArchivos.append(listarArchivos(archivo));
                } else {
                    listaArchivos.append(obtenerInfoArchivo(archivo)).append("\n");
                }
            }
        }
        return listaArchivos.toString();
    }

    /**
     * Obtiene informacion basica de un archivo.
     * 
     * @param archivo El archivo del que se quiere obtener informacion.
     * @return Una cadena con la ruta, tamaño y fecha de ultima modificación del archivo.
     */
    public String obtenerInfoArchivo(File archivo) {
        String rutaArchivo = archivo.getAbsolutePath();
        long tamanoKB = archivo.length() / 1024;
        String ultimaModificacion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(archivo.lastModified()));
        return String.format("%s (%d KB - %s)", rutaArchivo, tamanoKB, ultimaModificacion);
    }

    /**
     * Busca coincidencias de una cadena en los archivos de un directorio y sus subdirectorios.
     * 
     * @param dir El directorio donde buscar.
     * @param cadena La cadena a buscar.
     * @param caseSensitive Si la busqueda debe ser sensible a mayusculas y minusculas.
     * @param accentSensitive Si la busqueda debe ser sensible a acentos.
     * @return Una cadena con los resultados de la búsqueda.
     */
    public String buscarCoincidencias(File dir, String cadena, boolean caseSensitive, boolean accentSensitive) {
        StringBuilder resultados = new StringBuilder();
        File[] archivos = dir.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    resultados.append(buscarCoincidencias(archivo, cadena, caseSensitive, accentSensitive));
                } else {
                    int coincidencias = contarCoincidencias(archivo, cadena, caseSensitive, accentSensitive);
                    resultados.append(String.format("%s (%d coincidencias)\n", archivo.getAbsolutePath(), coincidencias));
                }
            }
        }
        return resultados.toString();
    }

    /**
     * Reemplaza coincidencias de una cadena por otra en los archivos de texto de un directorio.
     * 
     * @param dir El directorio donde realizar los reemplazos.
     * @param cadena La cadena a buscar.
     * @param nuevaCadena La cadena de reemplazo.
     * @param caseSensitive Si la busqueda debe ser sensible a mayusculas y minusculas.
     * @param accentSensitive Si la busqueda debe ser sensible a acentos.
     * @return Una cadena con los resultados de la operación de reemplazo.
     */
    public String reemplazarCoincidencias(File dir, String cadena, String nuevaCadena, boolean caseSensitive, boolean accentSensitive) {
        StringBuilder resultados = new StringBuilder();
        File[] archivos = dir.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.canRead() && archivo.getName().endsWith(".txt")) {
                    int reemplazos = reemplazarEnArchivo(archivo, cadena, nuevaCadena, caseSensitive, accentSensitive);
                    resultados.append(String.format("%s (%d reemplazos)\n", archivo.getAbsolutePath(), reemplazos));
                }
            }
        }
        return resultados.toString();
    }

    /**
     * Cuenta las coincidencias de una cadena en un archivo.
     * 
     * @param archivo El archivo donde buscar.
     * @param cadena La cadena a buscar.
     * @param caseSensitive Si la búsqueda debe ser sensible a mayusculas y minusculas.
     * @param accentSensitive Si la busqueda debe ser sensible a acentos.
     * @return El nmero de coincidencias encontradas.
     */
    private int contarCoincidencias(File archivo, String cadena, boolean caseSensitive, boolean accentSensitive) {
        if (!archivo.canRead() || !archivo.getName().endsWith(".txt")) {
            return 0;
        }
        int contador = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contador += contarEnLinea(linea, cadena, caseSensitive, accentSensitive);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contador;
    }

    private int contarEnLinea(String linea, String cadena, boolean caseSensitive, boolean accentSensitive) {
        if (!caseSensitive) {
            linea = linea.toLowerCase();
            cadena = cadena.toLowerCase();
        }
        if (!accentSensitive) {
            linea = normalize(linea);
            cadena = normalize(cadena);
        }
        int contador = 0;
        int index = 0;
        while ((index = linea.indexOf(cadena, index)) != -1) {
            contador++;
            index += cadena.length();
        }
        return contador;
    }

    private int reemplazarEnArchivo(File archivo, String cadena, String nuevaCadena, boolean caseSensitive, boolean accentSensitive) {
        int contador = 0;
        StringBuilder nuevoContenido = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String nuevaLinea = reemplazarEnLinea(linea, cadena, nuevaCadena, caseSensitive, accentSensitive);
                nuevoContenido.append(nuevaLinea).append("\n");
                contador += (nuevaLinea.length() - linea.length()) / (nuevaCadena.length() - cadena.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        
        String nuevoNombre = "MOD_" + archivo.getName();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(archivo.getParent(), nuevoNombre)))) {
            writer.write(nuevoContenido.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return contador;
    }

    private String reemplazarEnLinea(String linea, String cadena, String nuevaCadena, boolean caseSensitive, boolean accentSensitive) {
        if (!caseSensitive) {
            return linea.replaceAll("(?i)" + cadena, nuevaCadena);
        }
        if (!accentSensitive) {
            return normalize(linea).replaceAll(normalize(cadena), nuevaCadena);
        }
        return linea.replace(cadena, nuevaCadena);
    }

    private String normalize(String str) {
        return str.replaceAll("[á]", "a")
                  .replaceAll("[é]", "e")
                  .replaceAll("[í]", "i")
                  .replaceAll("[ó]", "o")
                  .replaceAll("[ú]", "u");
    }
}
