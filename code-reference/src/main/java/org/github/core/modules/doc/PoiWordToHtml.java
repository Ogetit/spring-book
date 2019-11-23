package org.github.core.modules.doc;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
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
 * doc转html，不同于docx转HTML
 */
public class PoiWordToHtml {
    //预览时临时目录
    private static String ROOTPATH = WebUtils.getRootPath("/updownload/tmp/doc/img");
    private static String ENCODING = "UTF-8";

    public static void main(String[] args) throws Exception {
        try {
            String p = convert2Html("E:/资料/电商/系统和数据库拆分以及数据互访.doc");
            System.out.print(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 需要转的doc的绝对路径
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String convert2Html(String fileName) throws Exception {
        String randpath = DateUtil.getNo(3);
        File f = new File(ROOTPATH + "/" + randpath);
        if (!f.exists()) {
            f.mkdirs();
        }
        HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(fileName));//WordToHtmlUtils.loadDoc(new FileInputStream(inputFile));
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content,
                                      PictureType pictureType, String suggestedName,
                                      float widthInches, float heightInches) {
                return suggestedName;
            }
        });
        wordToHtmlConverter.processDocument(wordDocument);
        //save pictures
        List pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                try {
                    pic.writeImageContent(new FileOutputStream(ROOTPATH + "/" + randpath + "/"
                            + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(out);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, ENCODING);
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        out.close();
        String file = "/" + randpath + "/" + randpath + ".html";
        FileUtils.writeStringToFile(new File(ROOTPATH + file), new String(out.toByteArray(),ENCODING), ENCODING);
        return file;
    }
}
