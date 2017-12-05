package com.pay.business.util.swtPay;

public class HexConver
{
  public static String Str2Hex(String str)
  {
    char[] chars = "0123456789ABCDEF".toCharArray();
    StringBuilder sb = new StringBuilder("");
    byte[] bs = str.getBytes();

    for (int i = 0; i < bs.length; i++)
    {
      int bit = (bs[i] & 0xF0) >> 4;
      sb.append(chars[bit]);
      bit = bs[i] & 0xF;
      sb.append(chars[bit]);
    }

    return sb.toString().trim();
  }

  public static String Hex2Str(String hexStr)
  {
    String str = "0123456789ABCDEF";
    char[] hexs = hexStr.toCharArray();
    byte[] bytes = new byte[hexStr.length() / 2];

    for (int i = 0; i < bytes.length; i++)
    {
      int n = str.indexOf(hexs[(2 * i)]) * 16;
      n += str.indexOf(hexs[(2 * i + 1)]);
      bytes[i] = (byte)(n & 0xFF);
    }
    return new String(bytes);
  }
}