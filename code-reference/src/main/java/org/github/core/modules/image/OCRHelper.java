package org.github.core.modules.image;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * OCR文字识别
 */
public class OCRHelper {
    private static final String LANG_OPTION = "-l";
    private static final String EOL = System.getProperty("line.separator");
    /**
     * 文件位置我防止在，项目同一路径
     */
    private static String tessPath = new File("C:\\Program Files (x86)\\Tesseract-OCR").getAbsolutePath();

    /**
     * @param imageFile 传入的图像文件
     * @return 识别后的字符串
     */
    public static String recognizeText(File imageFile) throws Exception {
        /**
         * 设置输出文件的保存的文件目录
         */
        File outputFile = new File(imageFile.getParentFile(), "output");

        StringBuffer strB = new StringBuffer();
        List<String> cmd = new ArrayList<String>();

        String os = StringUtils.trimToEmpty(System.getProperty("os.name"));

        if (os.toLowerCase().startsWith("win")) {
            cmd.add(tessPath + "\\tesseract");
        } else {
            cmd.add("tesseract");
        }
        cmd.add("");
        cmd.add(outputFile.getName());
        cmd.add(LANG_OPTION);
        // cmd.add("chi_sim");
        cmd.add("eng");

        ProcessBuilder pb = new ProcessBuilder();
        // Sets this process builder's working directory.
        pb.directory(imageFile.getParentFile());
        cmd.set(1, imageFile.getName());
        pb.command(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        // tesseract.exe 1.jpg 1 -l chi_sim
        // Runtime.getRuntime().exec("tesseract.exe 1.jpg 1 -l chi_sim");
        // the exit value of the process. By convention, 0 indicates normal termination.
        // System.out.println(cmd.toString());
        int w = process.waitFor();
        // 0代表正常退出
        if (w == 0) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(outputFile.getAbsolutePath() + ".txt"),
                    "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                strB.append(str).append(EOL);
            }
            in.close();
        } else {
            String msg;
            switch (w) {
                case 1:
                    msg = "Errors accessing files. There may be spaces in your image's filename.";
                    break;
                case 29:
                    msg = "Cannot recognize the image or its selected region.";
                    break;
                case 31:
                    msg = "Unsupported image format.";
                    break;
                default:
                    msg = "Errors occurred.";
            }
            throw new RuntimeException(msg);
        }
        new File(outputFile.getAbsolutePath() + ".txt").delete();
        return strB.toString();
    }
    public static void main(String[] args) throws Exception {
        recognizeText(new File("d:/170526180527680.tiff"));
    }
}