package org.github.core.modules.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * 创建验证码
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  github
 * @version  [版本号, 2016-9-5]
 * @see  [相关类/方法]
 * @since  [GITHUB]
 */
public class VerificationCodeUtil {
	/**
	 * 生成验证码图片
	 * @param code 验证码
	 * @param width 图片宽度
	 * @param height 图片高度
	 * @return BufferedImage [返回类型说明]
	 */
	public static BufferedImage createCode(String code,int width,int height,int... fontSize){
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics g = image.getGraphics();
		// 设定背景色
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		// 设定字体
		int textSize=18;
		if(fontSize!=null && fontSize.length==1){
			textSize=fontSize[0];
		}
		g.setFont(new Font("Times New Roman", Font.PLAIN, textSize));
		g.setColor(getRandColor(160, 200));
		// 随机产生干扰线，使图象中的认证码不易被其它程序探测到
		Random random = new Random();
		int lineNum=width * height/10;
		for (int i = 0; i < lineNum; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		int y=height/2+textSize/3;
		int length=code.length();
		int textWidth=textSize/2+3;
		int x=(width-textWidth*length)/2;
		for (int i = 0; i < length; i++) {
			// 将认证码显示到图象中
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(String.valueOf(code.charAt(i)), textWidth*i+x, y);
		}
		return image;
	}
	/**
	 * 输出图片到页面
	 * @param response
	 * @param image [参数说明]
	 * @throws IOException 
	 */
	public static void writeImg(HttpServletResponse response,BufferedImage image) throws IOException {
		ServletOutputStream out=response.getOutputStream();
		try{
			// 输出图象到页面
			ImageIO.write(image, "JPEG", response.getOutputStream());
		}finally{
			out.close();
		}
		
	}
	/**
	 * 生成验证码图片并输出到浏览器
	 * @param code 验证码
	 * @param width 图片宽度
	 * @param height 图片高度
	 * @param response
	 * @return BufferedImage [返回类型说明]
	 * @throws IOException 
	 */
	public static void createCode(String code,int width,int height,HttpServletResponse response,int... fontSize) throws IOException{
		BufferedImage image=createCode(code, width, height,fontSize);
		writeImg(response, image);
	}
	/**
	 * 获取随机颜色
	 */
	private static Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255){
			fc = 255;
		}
		if (bc > 255){
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}
