package org.github.core.modules.excel;

/**
 * 配置与excel表头
 *
 * @author 章磊
 */
public class ExcelTitleBean {
    /**
     * 单元格表头名
     *
     * @return
     */
    String value;

    /**
     * 单元格描叙信息
     *
     * @return
     */
    String description;

    /**
     * 单元格实例信息
     *
     * @return
     */
    String example;

    /**
     * 单元格合并的表头名，相同表头的合并在一起
     *
     * @return
     */
    String group;

    /**
     * 单元格颜色,相同group的颜色如果没有定义，则取第一个group的
     *
     * @return
     */
    String color;

    /**
     * 列的宽度0为隐藏,多少个汉字宽度，如果这列是8个汉字那么输入8，默认宽度是汉字个数宽度
     *
     * @return
     */
    String width;

    /**
     * 设置数据有效性
     * @return
     */
    String[] datavalid;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String[] getDatavalid() {
        return datavalid;
    }

    public void setDatavalid(String[] datavalid) {
        this.datavalid = datavalid;
    }
}
