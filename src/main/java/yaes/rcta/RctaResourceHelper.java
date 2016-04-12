package yaes.rcta;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

/**
 * This class implements basic IO functionalities
 * 
 * @author SaadKhan
 *
 */
public class RctaResourceHelper implements Serializable {

    private static final long serialVersionUID = 470637360393863415L;

    /**
     * Remove and recreate all the data directories
     */
    public static void flushFolders() {
        try {
            delete(constRCTA.logDir);
            delete(constRCTA.outputDir);
            delete(constRCTA.graphDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        constRCTA.logDir.mkdirs();
        constRCTA.outputDir.mkdirs();
        constRCTA.graphDir.mkdirs();
    }

    /**
     * Delete file or complete directory
     * 
     * @param file
     * @throws IOException
     */
    public static void delete(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                // directory is empty, then delete it
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println(
                            "Directory is deleted : " + file.getAbsolutePath());
                } else {
                    // list all the directory contents
                    String files[] = file.list();
                    for (String temp : files) {
                        // construct the file structure
                        File fileDelete = new File(file, temp);
                        // recursive delete
                        delete(fileDelete);
                    }
                    // check the directory again, if empty then delete it
                    if (file.list().length == 0) {
                        file.delete();
                        System.out.println("Directory is deleted : "
                                + file.getAbsolutePath());
                    }
                }
            } else {
                // if file, then delete it
                file.delete();
                System.out
                        .println("File is deleted : " + file.getAbsolutePath());
            }

        }
    }

    public static File getFile(String fileName) {
        URL url = RctaResourceHelper.class.getResource("/" + fileName);
        File file = new File(url.getFile());
        return file;
    }
}
