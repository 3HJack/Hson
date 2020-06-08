package com.hhh.hson.test;

import com.hhh.hson.annotation.Json;
import com.hhh.hson.annotation.SerializedName;

@Json
public class NoValueHsonExample extends BaseHsonExample {

  @SerializedName("specialKeyOne")
  public String special_key_1;

  @SerializedName(value = "specialKeyTwo", alternate = {"specialKeyThree","specialKeyFour"})
  public String special_key_2;

  // transient 标记的成员变量不参与序列化和反序列化
  public transient String ignore = "ignore";

  // static 标记的成员变量不参与序列化和反序列化
  public static String static_str = "static_str";

  // final 标记的成员变量不参与序列化和反序列化
  public final String final_str = "final_str";

  boolean bool_1;
  boolean bool_2;
  // 私有成员变量不参与序列化和反序列化，下同
  private boolean bool_3;
  private boolean bool_4;
  protected boolean bool_5;
  protected boolean bool_6;
  public boolean bool_7;
  public boolean bool_8;

  Boolean Bool_1;
  Boolean Bool_2;
  private Boolean Bool_3;
  private Boolean Bool_4;
  protected Boolean Bool_5;
  protected Boolean Bool_6;
  public Boolean Bool_7;
  public Boolean Bool_8;

  byte b_1;
  byte b_2;
  private byte b_3;
  private byte b_4;
  protected byte b_5;
  protected byte b_6;
  public byte b_7;
  public byte b_8;

  Byte B_1;
  Byte B_2;
  private Byte B_3;
  private Byte B_4;
  protected Byte B_5;
  protected Byte B_6;
  public Byte B_7;
  public Byte B_8;

  char c_1;
  char c_2;
  private char c_3;
  private char c_4;
  protected char c_5;
  protected char c_6;
  public char c_7;
  public char c_8;

  Character C_1;
  Character C_2;
  private Character C_3;
  private Character C_4;
  protected Character C_5;
  protected Character C_6;
  public Character C_7;
  public Character C_8;

  short s_1;
  short s_2;
  private short s_3;
  private short s_4;
  protected short s_5;
  protected short s_6;
  public short s_7;
  public short s_8;

  Short S_1;
  Short S_2;
  private Short S_3;
  private Short S_4;
  protected Short S_5;
  protected Short S_6;
  public Short S_7;
  public Short S_8;

  int i_1;
  int i_2;
  private int i_3;
  private int i_4;
  protected int i_5;
  protected int i_6;
  public int i_7;
  public int i_8;

  Integer I_1;
  Integer I_2;
  private Integer I_3;
  private Integer I_4;
  protected Integer I_5;
  protected Integer I_6;
  public Integer I_7;
  public Integer I_8;

  long l_1;
  long l_2;
  private long l_3;
  private long l_4;
  protected long l_5;
  protected long l_6;
  public long l_7;
  public long l_8;

  Long L_1;
  Long L_2;
  private Long L_3;
  private Long L_4;
  protected Long L_5;
  protected Long L_6;
  public Long L_7;
  public Long L_8;

  float f_1;
  float f_2;
  private float f_3;
  private float f_4;
  protected float f_5;
  protected float f_6;
  public float f_7;
  public float f_8;

  Float F_1;
  Float F_2;
  private Float F_3;
  private Float F_4;
  protected Float F_5;
  protected Float F_6;
  public Float F_7;
  public Float F_8;

  double d_1;
  double d_2;
  private double d_3;
  private double d_4;
  protected double d_5;
  protected double d_6;
  public double d_7;
  public double d_8;

  Double D_1;
  Double D_2;
  private Double D_3;
  private Double D_4;
  protected Double D_5;
  protected Double D_6;
  public Double D_7;
  public Double D_8;

  String str_1;
  String str_2;
  private String str_3;
  private String str_4;
  protected String str_5;
  protected String str_6;
  public String str_7;
  public String str_8;

  // 非静态内部类开发者必须初始化，否则运行时崩溃
  InnerClass Inn_1 = new InnerClass();
  InnerClass Inn_2 = new InnerClass();
  private InnerClass Inn_3 = new InnerClass();
  private InnerClass Inn_4 = new InnerClass();
  protected InnerClass Inn_5 = new InnerClass();
  protected InnerClass Inn_6 = new InnerClass();
  public InnerClass Inn_7 = new InnerClass();
  public InnerClass Inn_8 = new InnerClass();

  StaticInnerClass SInn_1;
  StaticInnerClass SInn_2;
  private StaticInnerClass SInn_3;
  private StaticInnerClass SInn_4;
  protected StaticInnerClass SInn_5;
  protected StaticInnerClass SInn_6;
  public StaticInnerClass SInn_7;
  public StaticInnerClass SInn_8;

  @Json
  public static class StaticInnerClass {

    String sinner_str_1;
    String sinner_str_2;
    private String sinner_str_3;
    private String sinner_str_4;
    protected String sinner_str_5;
    protected String sinner_str_6;
    public String sinner_str_7;
    public String sinner_str_8;
  }

  @Json
  public class InnerClass {
    String inner_str_1;
    String inner_str_2;
    private String inner_str_3;
    private String inner_str_4;
    protected String inner_str_5;
    protected String inner_str_6;
    public String inner_str_7;
    public String inner_str_8;
  }
}
