package org.github.core.modules.doc;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.github.core.modules.utils.WebUtils;

import java.io.*;
import java.util.Map;

/**
 * Created by Eric on 2017/1/16.
 */
public class ExportDoc {

    /**
     * 模板文件存储的规定目录
     */
    private static StringBuilder templateBaseFolder = new StringBuilder(WebUtils.getFileRootPath("/updownload/asms/mb/docx/"));
    /**
     * 系统ROOT下的目录
     */
    private static StringBuilder imgBaseFolder = new StringBuilder(WebUtils.getFileRootPath("/"));
    /**
     * 临时文件
     */
    private static StringBuilder imgExtractPath = new StringBuilder(WebUtils.getFileRootPath("/updownload/tmp/doc/img"));

    private ExportDoc() {
    }

    private enum FileType {
        DOCX, PDF, HTML;
    }

    /**
     * 把WORD DOCX格式模板，渲染数据并输出为DOCX格式
     *
     * @param template 模板文件名称 存储在 /updownload/asms/mb/docx/ 目录下如 updownload/asms/mb/docx/travel/gjContract.docx  则为travel/gjContract.docx
     * @param out      文件输出流
     * @param beanMap  存有bean的map  数据
     * @throws IOException
     * @throws XDocReportException
     */
    public static void genarateDocx(String template, OutputStream out, Map<String, Object> beanMap) throws Exception {
        genarateFile(FileType.DOCX, new StringBuilder(templateBaseFolder).append(template).toString(), out, beanMap, "");
    }

    /**
     * 不使用模板生成Docx文件的输出流
     *
     * @param uploadFilePath 文件路径
     * @param out
     * @throws Exception
     */
    public static void genarateDocx(String uploadFilePath, OutputStream out) throws Exception {
        genarateFile(FileType.DOCX, new StringBuilder(imgBaseFolder).append(uploadFilePath).toString(), out, null, "");
    }

    /**
     * 把WORD模板，渲染数据并输出为PDF格式
     *
     * @param template
     * @param out
     * @param beanMap
     * @throws Exception
     */
    public static void genaratePDF(String template, OutputStream out, Map<String, Object> beanMap) throws Exception {
        genarateFile(FileType.PDF, new StringBuilder(templateBaseFolder).append(template).toString(), out, beanMap, "");
    }

    /**
     * 不使用模板,把DOCX格式的word生成pdf文件的输出流
     *
     * @param uploadFilePath 文件路径
     * @param out
     * @throws Exception
     */
    public static void genaratePDF(String uploadFilePath, OutputStream out) throws Exception {
        genarateFile(FileType.PDF, new StringBuilder(imgBaseFolder).append(uploadFilePath).toString(), out, null, "");
    }

    /**
     * 生成HTML文件的输出流
     *
     * @param template 模板文件名称
     * @param out      输出流
     * @param beanMap  存有bean的map
     * @param baseURL  HTML源码中访问图片的URL路径的base部分，全部URL还要加上word/media/
     * @throws IOException
     * @throws XDocReportException
     */
    public static void genarateHTML(String template, OutputStream out, Map<String, Object> beanMap, String baseURL) throws Exception {
        genarateFile(FileType.HTML, new StringBuilder(templateBaseFolder).append(template).toString(), out, beanMap, baseURL);
    }

    /**
     * 不使用模板把DOCX输出为HTML
     *
     * @param uploadFilePath
     * @param out
     * @param baseURL
     * @throws Exception
     */

    public static void genarateHTML(String uploadFilePath, OutputStream out, String baseURL) throws Exception {
        genarateFile(FileType.HTML, new StringBuilder(imgBaseFolder).append(uploadFilePath).toString(), out, null, baseURL);
    }

    /**
     * 生成各种文件的输出流
     *
     * @param type         文件类型
     * @param templatePath 模板文件路径或者预览文件路径
     * @param out          文件输出流
     * @param beanMap      存有bean的map
     * @param baseURL      HTML源码中访问图片的URL路径的base部分，全部URL还要加上word/media/
     * @throws IOException
     * @throws XDocReportException
     */
    private static void genarateFile(FileType type, String templatePath, OutputStream out, Map<String, Object> beanMap, String baseURL) throws Exception {
        // 1) Load Docx file by filling Freemarker template engine and cache
        // it to the registry
        InputStream in = new FileInputStream(templatePath);
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);
        IContext context = report.createContext();
        if (beanMap != null) {
            context.putMap(beanMap);
            loadImgMap(context, report, beanMap);
        }
        if (type == FileType.DOCX) {
            report.process(context, out);
        } else if (type == FileType.PDF) {
            report.convert(context, Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF), out);
        } else if (type == FileType.HTML) {
            XHTMLOptions options = XHTMLOptions.create();
            options.setExtractor(new FileImageExtractor(new File(imgExtractPath.toString())));
            options.URIResolver(new BasicURIResolver(baseURL));
            Options opt = Options.getTo(ConverterTypeTo.XHTML).via(ConverterTypeVia.XWPF);
            opt.subOptions(options);
            report.convert(context, opt, out);
        }
        out.flush();
    }

    /**
     * 加载image Map的信息
     *
     * @param context context对象
     * @param report  report对象
     * @param imgMap  存有图片信息的map
     * @throws IOException
     */
    private static void loadImgMap(IContext context, IXDocReport report, Map<String, Object> imgMap) throws IOException {
        FieldsMetadata metadata = report.getFieldsMetadata();
        if (null == metadata) {
            metadata = report.createFieldsMetadata();
        }
        for (Map.Entry<String, ?> entry : imgMap.entrySet()) {
            Object value = entry.getValue();
            IImageProvider imageProvider = null;
            if(value != null ) {
                if ((value instanceof String) && value.toString().startsWith("img:")) {
                    StringBuilder imgPath = new StringBuilder(imgBaseFolder).append(value.toString().replace("img:", ""));
                    InputStream imgIn = new FileInputStream(imgPath.toString());
                    imageProvider = new ByteArrayImageProvider(imgIn);
                }else if(value instanceof byte[]){
                    imageProvider = new ByteArrayImageProvider((byte[])value);
                }
                if (imageProvider != null) {
                    context.put(entry.getKey(), imageProvider);
                    metadata.addFieldAsImage(entry.getKey());
                }
            }
        }
    }
}
