package com.hhh.hson.test;

import com.hhh.hson.annotation.Json;
import com.hhh.hson.annotation.SerializedName;

@Json
public class HsonExample extends BaseHsonExample {

  @SerializedName("specialKeyOne")
  public String special_key_1 = "special_key_1";

  @SerializedName(value = "specialKeyTwo", alternate = {"specialKeyThree","specialKeyFour"})
  public String special_key_2 = "special_key_2";

  // transient 标记的成员变量不参与序列化和反序列化
  public transient String ignore = "ignore";

  // static 标记的成员变量不参与序列化和反序列化
  public static String static_str = "static_str";

  // final 标记的成员变量不参与序列化和反序列化
  public final String final_str = "final_str";

  boolean bool_1;
  boolean bool_2 = true;
  // 私有成员变量不参与序列化和反序列化，下同
  private boolean bool_3;
  private boolean bool_4 = true;
  protected boolean bool_5;
  protected boolean bool_6 = true;
  public boolean bool_7;
  public boolean bool_8 = true;

  Boolean Bool_1 = false;
  Boolean Bool_2 = true;
  private Boolean Bool_3 = false;
  private Boolean Bool_4 = true;
  protected Boolean Bool_5 = false;
  protected Boolean Bool_6 = true;
  public Boolean Bool_7 = false;
  public Boolean Bool_8 = true;

  byte b_1 = 1;
  byte b_2 = 2;
  private byte b_3 = 3;
  private byte b_4 = 4;
  protected byte b_5 = 5;
  protected byte b_6 = 6;
  public byte b_7 = 7;
  public byte b_8 = 8;

  Byte B_1 = 1;
  Byte B_2 = 2;
  private Byte B_3 = 3;
  private Byte B_4 = 4;
  protected Byte B_5 = 5;
  protected Byte B_6 = 6;
  public Byte B_7 = 7;
  public Byte B_8 = 8;

  char c_1 = 'a';
  char c_2 = 'b';
  private char c_3 = 'c';
  private char c_4 = 'd';
  protected char c_5 = 'e';
  protected char c_6 = 'f';
  public char c_7 = 'g';
  public char c_8 = 'h';

  Character C_1 = 'a';
  Character C_2 = 'b';
  private Character C_3 = 'c';
  private Character C_4 = 'd';
  protected Character C_5 = 'e';
  protected Character C_6 = 'f';
  public Character C_7 = 'g';
  public Character C_8 = 'h';

  short s_1 = 1;
  short s_2 = 2;
  private short s_3 = 3;
  private short s_4 = 4;
  protected short s_5 = 5;
  protected short s_6 = 6;
  public short s_7 = 7;
  public short s_8 = 8;

  Short S_1 = 1;
  Short S_2 = 2;
  private Short S_3 = 3;
  private Short S_4 = 4;
  protected Short S_5 = 5;
  protected Short S_6 = 6;
  public Short S_7 = 7;
  public Short S_8 = 8;

  int i_1 = 1;
  int i_2 = 2;
  private int i_3 = 3;
  private int i_4 = 4;
  protected int i_5 = 5;
  protected int i_6 = 6;
  public int i_7 = 7;
  public int i_8 = 8;

  Integer I_1 = 1;
  Integer I_2 = 2;
  private Integer I_3 = 3;
  private Integer I_4 = 4;
  protected Integer I_5 = 5;
  protected Integer I_6 = 6;
  public Integer I_7 = 7;
  public Integer I_8 = 8;

  long l_1 = 1L;
  long l_2 = 2L;
  private long l_3 = 3L;
  private long l_4 = 4L;
  protected long l_5 = 5L;
  protected long l_6 = 6L;
  public long l_7 = 7L;
  public long l_8 = 8L;

  Long L_1 = 1L;
  Long L_2 = 2L;
  private Long L_3 = 3L;
  private Long L_4 = 4L;
  protected Long L_5 = 5L;
  protected Long L_6 = 6L;
  public Long L_7 = 7L;
  public Long L_8 = 8L;

  float f_1 = 1.0f;
  float f_2 = 2.0f;
  private float f_3 = 3.0f;
  private float f_4 = 4.0f;
  protected float f_5 = 5.0f;
  protected float f_6 = 6.0f;
  public float f_7 = 7.0f;
  public float f_8 = 8.0f;

  Float F_1 = 1.0f;
  Float F_2 = 2.0f;
  private Float F_3 = 3.0f;
  private Float F_4 = 4.0f;
  protected Float F_5 = 5.0f;
  protected Float F_6 = 6.0f;
  public Float F_7 = 7.0f;
  public Float F_8 = 8.0f;

  double d_1 = 1.0;
  double d_2 = 2.0;
  private double d_3 = 3.0;
  private double d_4 = 4.0;
  protected double d_5 = 5.0;
  protected double d_6 = 6.0;
  public double d_7 = 7.0;
  public double d_8 = 8.0;

  Double D_1 = 1.0;
  Double D_2 = 2.0;
  private Double D_3 = 3.0;
  private Double D_4 = 4.0;
  protected Double D_5 = 5.0;
  protected Double D_6 = 6.0;
  public Double D_7 = 7.0;
  public Double D_8 = 8.0;

  String str_1 = "str_1";
  String str_2 = "str_2";
  private String str_3 = "str_3";
  private String str_4 = "str_4";
  protected String str_5 = "str_5";
  protected String str_6 = "str_6";
  public String str_7 = "str_7";
  public String str_8 = "str_8";

  // 非静态内部类开发者必须初始化，否则运行时崩溃
  InnerClass Inn_1 = new InnerClass();
  InnerClass Inn_2 = new InnerClass();
  private InnerClass Inn_3 = new InnerClass();
  private InnerClass Inn_4 = new InnerClass();
  protected InnerClass Inn_5 = new InnerClass();
  protected InnerClass Inn_6 = new InnerClass();
  public InnerClass Inn_7 = new InnerClass();
  public InnerClass Inn_8 = new InnerClass();

  StaticInnerClass SInn_1 = new StaticInnerClass();
  StaticInnerClass SInn_2 = new StaticInnerClass();
  private StaticInnerClass SInn_3 = new StaticInnerClass();
  private StaticInnerClass SInn_4 = new StaticInnerClass();
  protected StaticInnerClass SInn_5 = new StaticInnerClass();
  protected StaticInnerClass SInn_6 = new StaticInnerClass();
  public StaticInnerClass SInn_7 = new StaticInnerClass();
  public StaticInnerClass SInn_8 = new StaticInnerClass();

  @Json
  public static class StaticInnerClass {

    String sinner_str_1 = "sinner_str_1";
    String sinner_str_2 = "sinner_str_2";
    private String sinner_str_3 = "sinner_str_3";
    private String sinner_str_4 = "sinner_str_4";
    protected String sinner_str_5 = "sinner_str_5";
    protected String sinner_str_6 = "sinner_str_6";
    public String sinner_str_7 = "sinner_str_7";
    public String sinner_str_8 = "sinner_str_8";
  }

  @Json
  public class InnerClass {
    String inner_str_1 = "inner_str_1";
    String inner_str_2 = "inner_str_2";
    private String inner_str_3 = "inner_str_3";
    private String inner_str_4 = "inner_str_4";
    protected String inner_str_5 = "inner_str_5";
    protected String inner_str_6 = "inner_str_6";
    public String inner_str_7 = "inner_str_7";
    public String inner_str_8 = "inner_str_8";
  }
}
