package org.github.core.modules.doc;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.usermodel.Picture;
import org.github.core.modules.utils.DateUtil;
import org.github.core.modules.utils.WebUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * Created by github on 2017/5/22.
 */
public class PoiExcelToHtml {
    //预览时临时目录
    private static String ROOTPATH = WebUtils.getRootPath("/updownload/tmp/doc/img");
    private static String ENCODING = "UTF-8";

    public static String convert2Excel(String fileName) throws Exception {
        String randpath = DateUtil.getNo(3);
        File f = new File(ROOTPATH + "/" + randpath);
        if (!f.exists()) {
            f.mkdirs();
        }
        InputStream input = new FileInputStream(fileName);
        HSSFWorkbook excelBook = new HSSFWorkbook(input);
        ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        excelToHtmlConverter.processWorkbook(excelBook);
        List pics = excelBook.getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                try {
                    pic.writeImageContent(new FileOutputStream(ROOTPATH + "/" + randpath + "/" + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Document htmlDocument = excelToHtmlConverter.getDocument();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, ENCODING);
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        outStream.close();
        String content = new String(outStream.toByteArray(),ENCODING);
        String file = "/" + randpath + "/" + randpath + ".html";
        FileUtils.writeStringToFile(new File(ROOTPATH + file), content, ENCODING);
        return file;
    }
}
