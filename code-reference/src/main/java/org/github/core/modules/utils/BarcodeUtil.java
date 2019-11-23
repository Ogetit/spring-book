//package org.github.core.modules.utils;
//
//import org.apache.commons.lang.StringUtils;
//import org.jbarcode.JBarcode;
//import org.jbarcode.encode.Code128Encoder;
//import org.jbarcode.encode.Code39Encoder;
//import org.jbarcode.encode.EAN13Encoder;
//import org.jbarcode.encode.Interleaved2of5Encoder;
//import org.jbarcode.paint.BaseLineTextPainter;
//import org.jbarcode.paint.EAN13TextPainter;
//import org.jbarcode.paint.WideRatioCodedPainter;
//import org.jbarcode.paint.WidthCodedPainter;
//import org.jbarcode.util.ImageUtil;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileOutputStream;
///**
// * <生成一维码>
// */
//public class BarcodeUtil {
//	/**
//	 * 生成条形码
//	 * @param code //生成条形码的字符串
//	 * @param imgPath//条形码图片的路径 请出入png格式
//     */
//	public static void makeBarcode(String code,String imgPath) {
//		try {
//			JBarcode localJBarcode = initCode39();
//			BufferedImage localBufferedImage = localJBarcode.createBarcode(code);
//			// 保存图片
//			saveToPNG(localBufferedImage, imgPath);
//		} catch (Exception localException) {
//			localException.printStackTrace();
//		}
//	}
//    /**
//     * 初始化条形码对象
//     * code39 支持英文和数字
//     * Code128Encoder 编码
//     * 参考【http://blog.csdn.net/pengjianhuan88/article/details/12167737】
//     */
//    private static JBarcode initCode39() {
//        // 默认生成code39类型条形码
//        JBarcode localJBarcode = new JBarcode(Code39Encoder.getInstance(),
//                    WideRatioCodedPainter.getInstance(),
//                    BaseLineTextPainter.getInstance());
//        initEncoderCode128(localJBarcode);
//        return localJBarcode;
//    }
//
//
//	private static void saveToPNG(BufferedImage paramBufferedImage, String imgPath) throws Exception {
//		 saveToFile(paramBufferedImage, imgPath);
//	}
//
//	/**
//	 * 保存图片，并返回图片存储地址
//	 */
//	private static void saveToFile(BufferedImage paramBufferedImage, String imgpath) throws Exception{
//		File f = new File(imgpath);
//		File parent = f.getParentFile();
//		// 创建保存的文件夹
//		if (!parent.exists()) {
//			parent.mkdirs();
//		}
//		String tpGs=StringUtils.substringAfterLast(imgpath,".");//图片格式
//		// 生成图片文件
//		FileOutputStream localFileOutputStream = new FileOutputStream(f);
//		ImageUtil.encodeAndWrite(paramBufferedImage,tpGs, localFileOutputStream, 96, 96);
//		localFileOutputStream.close();
//	}
//    private static void initEncoderCode128(JBarcode localJBarcode) {
//        localJBarcode.setEncoder(Code128Encoder.getInstance());
//        localJBarcode.setPainter(WidthCodedPainter.getInstance());
//        localJBarcode.setTextPainter(BaseLineTextPainter.getInstance());
//        localJBarcode.setShowText(true);// 显示图片下字符串内容
//        localJBarcode.setShowCheckDigit(true);// 显示字符串内容中是否显示检查码内容
//        localJBarcode.setCheckDigit(false);// 不生成检查码
//    }
//
//}