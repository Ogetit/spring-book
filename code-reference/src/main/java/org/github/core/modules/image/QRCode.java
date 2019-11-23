package org.github.core.modules.image;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by github on 2017/5/15.
 * 生成和解析二维码
 */
public class QRCode {
    private final static Logger logger = LoggerFactory.getLogger(QRCode.class);

    private QRCode() {

    }

    public static void createCode(String content, int width, int height, OutputStream output) throws Exception {
        try {
            logger.info("生成二维码", content);
            // 图像类型
            String format = "png";
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            // 生成矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            // 输出图像
            MatrixToImageWriter.writeToStream(bitMatrix, format, output);
        } catch (Exception e) {
            logger.error("生成二维码失败", e);
            throw e;
        }
    }

    public static String parseCode(InputStream picInputStream) throws Exception {
        try {
            BufferedImage image = ImageIO.read(picInputStream);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            // 对图像进行解码
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);
            if (result != null) {
                logger.info("解析识别二维码", result.getText());
                return result.getText();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("解析识别二维码", e);
            throw e;
        }
    }
}
