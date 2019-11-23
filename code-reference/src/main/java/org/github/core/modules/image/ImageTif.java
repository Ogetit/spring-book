//package org.github.core.modules.image;
//
//import com.sun.media.jai.codec.ImageCodec;
//import com.sun.media.jai.codec.ImageEncoder;
//import com.sun.media.jai.codec.JPEGEncodeParam;
//import com.sun.media.jai.codec.TIFFEncodeParam;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.rendering.ImageType;
//import org.apache.pdfbox.rendering.PDFRenderer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.imageio.ImageIO;
//import javax.media.jai.JAI;
//import javax.media.jai.RenderedOp;
//import java.awt.*;
//import java.awt.geom.AffineTransform;
//import java.awt.image.AffineTransformOp;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by github on 2017/5/15.
// * 把PDF文件转为TIF格式的图片,用于发送传真
// */
//public class ImageTif {
//    private final static Logger logger = LoggerFactory.getLogger(ImageTif.class);
//
//    private ImageTif() {
//
//    }
//
//    /**
//     * 把PDF转为TIF 黑白的
//     *
//     * @param pdfInput
//     * @param tifOutput
//     * @throws Exception
//     */
//    public static void pdf2Tiff(InputStream pdfInput, OutputStream tifOutput) throws Exception {
//        try {
//            if (pdfInput == null) {
//                return;
//            }
//            PDDocument doc = PDDocument.load(pdfInput);
//            int pageCount = doc.getNumberOfPages();
//            List<BufferedImage> piclist = new ArrayList();
//            for (int i = 0; i < pageCount; i++) {
//                BufferedImage image = new PDFRenderer(doc).renderImageWithDPI(i, 250, ImageType.BINARY);//Photometric (1 Short): MinIsBlack
//                piclist.add(image);
//            }
//            doc.close();
//            tif2Marge(piclist, tifOutput);
//        } catch (Exception e) {
//            logger.error("PDF转TIFF失败", e);
//            throw e;
//        }
//    }
//
//
//    private static void tif2Marge(List<BufferedImage> piclist, OutputStream os) throws Exception {
//        if (piclist == null || piclist.size() < 1) {
//            return;
//        }
//        TIFFEncodeParam param = new TIFFEncodeParam();
//        param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP3_2D); //Compression (1 Short): Group 3 Fax (aka CCITT FAX3)
//        List<BufferedImage> piclistnew = new ArrayList<BufferedImage>();
//        for (int i = 1; i < piclist.size(); i++) {
//            piclistnew.add(piclist.get(i));
//        }
//        param.setExtraImages(piclistnew.iterator());
//        ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", os, param);
//        encoder.encode(piclist.get(0));
//    }
//
//
//    /**
//     * 将jpg格式转化为tif格式。
//     *
//     * @param srcFile  需要装换的源文件
//     * @param descFile 装换后的转存文件
//     * @throws Exception
//     */
//    public void jpg2tif(String srcFile, String descFile) throws Exception {
//        RenderedOp src = JAI.create("fileload", srcFile);
//        OutputStream os = new FileOutputStream(descFile);
//        TIFFEncodeParam param = new TIFFEncodeParam();
//        param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
//        ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", os, param);
//        encoder.encode(src);
//        os.close();
//    }
//
//    /**
//     * 把tif转为jpg用于页面显示
//     *
//     * @param srcFile
//     * @param descFile
//     * @throws Exception
//     */
//    public static void tif2jpg(String srcFile, String descFile) throws Exception {
//        RenderedOp src2 = JAI.create("fileload", srcFile);
//        OutputStream os2 = new FileOutputStream(descFile);
//        JPEGEncodeParam param = new JPEGEncodeParam();
//        //指定格式类型，jpg 属于 JPEG 类型
//        ImageEncoder enc2 = ImageCodec.createImageEncoder("JPEG", os2, param);
//        enc2.encode(src2);
//        os2.close();
//    }
//
//    public static void zoomImage(String src, String dest, int w, int h) throws Exception {
//        double wr = 0, hr = 0;
//        File srcFile = new File(src);
//        File destFile = new File(dest);
//        BufferedImage bufImg = ImageIO.read(srcFile);
//        Image Itemp = bufImg.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
//        wr = w * 1.0 / bufImg.getWidth();
//        hr = h * 1.0 / bufImg.getHeight();
//        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
//        BufferedImage bfItemp = ato.filter(bufImg, null);
//        try {
//            ImageIO.write(bfItemp, dest.substring(dest.lastIndexOf(".") + 1), destFile);
//        } catch (Exception e) {
//            logger.error("缩放图片失败", e);
//            throw e;
//        }
//    }
//}
