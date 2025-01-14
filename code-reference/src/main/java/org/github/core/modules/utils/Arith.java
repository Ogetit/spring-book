package org.github.core.modules.utils;

import java.math.BigDecimal;

/**
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精 确的浮点数运算，包括加减乘除和四舍五入。
 */

public class Arith {

	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 10;

	// 这个类不能实例化
	private Arith() {
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		// BigDecimal b = new BigDecimal(Double.toString(v));
		// BigDecimal one = new BigDecimal("1");
		// return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		return new BigDecimal(Double.toString(v)).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

    /**
     * 保留小数位四舍五入，小数位不足补0
     *
     * @param v     四舍五入的对像 123.699
     * @param cacle 保留小数位 2
     * @return 123.70
     */
    public static String round(String v, int cacle) {
        return new BigDecimal(v).setScale(cacle, BigDecimal.ROUND_HALF_UP).toString();
    }

	/**
	 * 提供精确的小数位保留精度舍位处理。
	 * 
	 * @param v
	 *            需要舍位的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 舍位后的结果
	 */
	public static double cut(double v, int scale) {
		// BigDecimal b = new BigDecimal(Double.toString(v));
		// BigDecimal one = new BigDecimal("1");
		// return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		return new BigDecimal(Double.toString(v)).setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
	}
	/**
	 * 票价精确到 10位 传 -1
	 * 
	 * @param v
	 * @param scale
	 * @return
	 */
	public static int round2Int(double v, int scale) {
		return new BigDecimal(Double.toString(v)).setScale(scale, BigDecimal.ROUND_HALF_UP).intValue();
	}

	/**
	 * 将科学计数法的DOUBLE转变为正常表示法
	 * 
	 * @param s
	 *            double
	 * @return String
	 */
	public static String getDstr(double s) {
		try {
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			return nf.format(s);
		} catch (Exception es) {
			return "";
		}
	}

	public static String getDstr(String s) {
		try {
			Double d = new Double(s);
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			return nf.format(d);
		} catch (Exception es) {
			return "";
		}
	}

	/**
	 * 把传入的一个对象转为double
	 * 
	 * @param o
	 * @return [参数说明]
	 * 
	 * @return Double [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static Double Obj2Double(Object o) {
		if (o == null) {
			return 0.0;
		}
		double ddd = 0.0;
		if (o instanceof Integer) {
			Integer i = (Integer) o;
			ddd = i.doubleValue();
		} else if (o instanceof Double) {
			Double d = (Double) o;
			ddd = d.doubleValue();
		} else if (o instanceof BigDecimal) {
			BigDecimal b = (BigDecimal) o;
			ddd = b.doubleValue();
		} else if (o instanceof Float) {
			Float f = (Float) o;
			ddd = f.doubleValue();
		} else if (o instanceof Number) {
			Number n = (Number) o;
			ddd = n.doubleValue();
		} else {
			ddd = 0.0;
		}
		return Arith.round(ddd, 2);
	}

	/**
	 * 求和
	 * 
	 * @param v1
	 *            多个数字的数组
	 * 
	 * @return double [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static double sum(double... v1) {
		double v = 0;
		if (v1 != null) {
			for (double one : v1) {
				v = add(v, one);
			}
		}
		return v;
	}

	public static void main(String[] args) {
		double cwpj = 1.0;
		int pj = new Double(Arith.round((cwpj / 10), 0) * 10).intValue();
		//System.out.println(pj);
		
		//System.out.println(new BigDecimal(Double.toString(cwpj)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
		
		//System.out.println(NumberUtils.toInt(cwpj+""));

	}

}

/*
 * 在Java中实现浮点数的精确计算
 * 
 * 问题的提出：
 * 
 * 如果我们编译运行下面这个程序会看到什么？
 * 
 * 
 * 你没有看错！结果确实是 0.060000000000000005 0.5800000000000001 401.49999999999994 1.2329999999999999
 * Java中的简单浮点数类型float和double不能够进行运算。不光是Java，在其它很多编程语言中也有这样的问题。在大多数情况下，计算的结果是准确的，但是多试几次（可以做一个循环）就可以试出类似上面的错误。现在终于理解为什么要有BCD码了。
 * 这个问题相当严重，如果你有9.999999999999元，你的计算机是不会认为你可以购买10元的商品的。 在有的编程语言中提供了专门的货币类型来处理这种情况，但是Java没有。现在让我们看看如何解决这个问题。
 * 
 * 
 * 四舍五入 我们的第一个反应是做四舍五入。Math类中的round方法不能设置保留几位小数，我们只能象这样（保留两位）： public double round(double value){ return
 * Math.round(value*100)/100.0; }
 * 
 * 非常不幸，上面的代码并不能正常工作，给这个方法传入4.015它将返回4.01而不是4.02，如我们在上面看到的
 * 
 * 4.015*100=401.49999999999994
 * 
 * 因此如果我们要做到精确的四舍五入，不能利用简单类型做任何运算
 * 
 * java.text.DecimalFormat也不能解决这个问题：
 * 
 * 输出是4.02
 * 
 * 
 * 
 * BigDecimal 在《Effective
 * Java》这本书中也提到这个原则，float和double只能用来做科学计算或者是工程计算，在商业计算中我们要用java.math.BigDecimal。BigDecimal一共有4个够造方法，我们不关心用BigInteger来够造的那两个，那么还有两个，它们是：
 * 
 * BigDecimal(double val)
 * 
 * Translates a double into a BigDecimal.
 * 
 * BigDecimal(String val)
 * 
 * Translates the String repre sentation of a BigDecimal into a BigDecimal.
 * 
 * 上面的API简要描述相当的明确，而且通常情况下，上面的那一个使用起来要方便一些。我们可能想都不想就用上了，会有什么问题呢？等到出了问题的时候，才发现上面哪个够造方法的详细说明中有这么一段：
 * 
 * Note: the results of this constructor can be somewhat unpredictable. One might assume that new BigDecimal(.1) is
 * exactly equal to .1, but it is actually equal to .1000000000000000055511151231257827021181583404541015625. This is so
 * because .1 cannot be represented exactly as a double (or, for that matter, as a binary fraction of any finite
 * length). Thus, the long value that is being passed in to the constructor is not exactly equal to .1, appearances
 * nonwithstanding. The (String) constructor, on the other hand, is perfectly predictable: new BigDecimal(".1") is
 * exactly equal to .1, as one would expect. Therefore, it is generally recommended that the (String) constructor be
 * used in preference to this one.
 * 
 * 
 * 原来我们如果需要精确计算，非要用String来够造BigDecimal不可！在《Effective Java》一书中的例子是用String来够造BigDecimal的，但是书上却没有强调这一点，这也许是一个小小的失误吧。
 * 
 * 解决方案
 * 
 * 现在我们已经可以解决这个问题了，原则是使用BigDecimal并且一定要用String来够造。
 * 
 * 但是想像一下吧，如果我们要做一个加法运算，需要先将两个浮点数转为String，然后够造成BigDecimal，在其中一个上调用add方法，传入另一个作为参数，然后把运算的结果（BigDecimal）再转换为浮点数。你能够忍受这么烦琐的过程吗？下面我们提供一个工具类Arith来简化操作。它提供以下静态方法，包括加减乘除和四舍五入：
 */
