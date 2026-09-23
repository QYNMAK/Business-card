package com.pfh.crm.dashboard.common.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.Builder;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class QRUtil {

    private int height;

    private int width;

    private String format = "gif";

//    public static void main(String[] args) {
//        try {
//            QRUtil build = QRUtil.builder().width(300).height(300).build();
//            // build.qrEncode();
//
//            Result result = build.qrReader(new File("/Users/mycom/Documents/pengfh/work/develop/postman/zxing1.gif"));
//
//            //输出相关的二维码信息
//            System.out.println("解析结果：" + result.toString());
//            System.out.println("二维码格式类型：" + result.getBarcodeFormat());
//            System.out.println("二维码文本内容：" + result.getText());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 根据内容、logoURL 生成二维码
     *
     * @param content 内容
     * @param logoUrl logo
     * @return 二维码
     * @throws WriterException 生成失败
     * @throws IOException 生成失败
     */
    public BufferedImage qrEncode(String content, URL logoUrl) throws WriterException, IOException {
        return qrEncode(content,ImageIO.read(logoUrl));
    }

    /**
     * 根据内容、logoURL 生成二维码
     *
     * @param content 内容
     * @param logo logo
     * @return 二维码
     * @throws WriterException 生成失败
     * @throws IOException 生成失败
     */
    public BufferedImage qrEncode(String content, BufferedImage logo) throws WriterException, IOException {


        Map<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);


        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        return logoMatrix(MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig), logo);
    }

    /**
     * 生成二维码
     */
    public void qrEncode() throws WriterException, IOException {
        String content = "个人博客：https://www.cnblogs.com/huanzi-qch/";//二维码内容

        BufferedImage bufferedImage = qrEncode(content, ImageIO.read(new URL("https://wx.qlogo.cn/mmopen/vi_32/rPicaIz63lJZHqS0icVeWTF6vguuTVdY41ADRAZdhTtLPO7qg3YC5BM5JOqRjwOlfMzPicBq4aqEjayB4IzsCdM7w/132")));
        ImageIO.write(bufferedImage, "gif", new File("/Users/mycom/Documents/pengfh/work/develop/postman/zxing1.gif"));//输出带logo图片
        System.out.println("输出成功.");
    }

    /**
     * 识别二维码
     */
    public Result qrReader(File file) throws IOException, NotFoundException {
        MultiFormatReader formatReader = new MultiFormatReader();
        //读取指定的二维码文件
        BufferedImage bufferedImage = ImageIO.read(file);

        return qrReader(bufferedImage);

    }

    /**
     * 识别网络二维码
     *
     * @param url url
     * @return Result
     * @throws IOException 读取失败
     */
    public Result qrReader(URL url) throws IOException,NotFoundException {
        MultiFormatReader formatReader = new MultiFormatReader();
        //读取指定的二维码文件
        BufferedImage bufferedImage = ImageIO.read(url);
        return qrReader(bufferedImage);

    }

    /**
     * 识别网络二维码
     *
     * @param qrImg 二维码
     * @return Result
     * @throws NotFoundException 文件存在
     */
    public Result qrReader(BufferedImage qrImg) throws NotFoundException {
        MultiFormatReader formatReader = new MultiFormatReader();
        //读取指定的二维码文件
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(qrImg)));
        //定义二维码参数
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        qrImg.flush();
        return formatReader.decode(binaryBitmap, hints);
    }

    /**
     * 二维码添加logo
     *
     * @param matrixImage 源二维码图片
     * @param logo        logo图片
     * @return 返回带有logo的二维码图片
     * 参考：https://blog.csdn.net/weixin_39494923/article/details/79058799
     */
    private BufferedImage logoMatrix(BufferedImage matrixImage, BufferedImage logo) throws IOException {

        Graphics2D g2 = matrixImage.createGraphics();

        int matrixWidth = matrixImage.getWidth();
        int matrixHeight = matrixImage.getHeight();


        g2.drawImage(logo, matrixWidth / 5 * 2, matrixHeight / 5 * 2, matrixWidth / 5, matrixHeight / 5, null);//绘制
        BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);

        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth / 5.0F * 2, matrixHeight / 5.0F * 2, matrixWidth / 5.0F, matrixHeight / 5.0F, 20, 20);
        g2.setColor(Color.white);
        g2.draw(round);

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth / 5.0F * 2 + 2, matrixHeight / 5.0F * 2 + 2, matrixWidth / 5.0F - 4, matrixHeight / 5.0F - 4, 20, 20);
        g2.setColor(new Color(128, 128, 128));
        g2.draw(round2);

        g2.dispose();
        matrixImage.flush();

        return matrixImage;
    }
}