/*
 * Copyright (C) 2013 Christopher Boyd
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package us.cboyd.shared;

/** 
 * W3C compatible X11 colors
 * I felt like there weren't enough pre-defined colors,
 * so I decided to just implement this list.
 * 
 * These colors taken from:
 * http://en.wikipedia.org/wiki/Web_colors#X11_color_names
 * 
 * @author Christopher Boyd
 *
 */

public class X11Color {
    //HTML Colors
	//Pink
    public static final int Pink 			= 0xFFFFC0CB;
    public static final int LightPink 		= 0xFFFFB6C1;
    public static final int HotPink 		= 0xFFFF69B4;
    public static final int DeepPink 		= 0xFFFF1493;
    public static final int PaleVioletRed 	= 0xFFDB7093;
    public static final int MediumVioletRed = 0xFFC71585;
    //Red
    public static final int LightSalmon 	= 0xFFFFA07A;
    public static final int Salmon 			= 0xFFFA8072;
    public static final int DarkSalmon 		= 0xFFE9967A;
    public static final int LightCoral 		= 0xFFF08080;
    public static final int IndianRed 		= 0xFFCD5C5C;
    public static final int Crimson 		= 0xFFDC143C;
    public static final int FireBrick 		= 0xFFB22222;
    public static final int DarkRed 		= 0xFF8B0000;
    public static final int Red         	= 0xFFFF0000;
    //Orange
    public static final int OrangeRed 		= 0xFFFF4500;
    public static final int Tomato 			= 0xFFFF6347;
    public static final int Coral 			= 0xFFFF7F50;
    public static final int DarkOrange 		= 0xFFFF8C00;
    public static final int Orange 			= 0xFFFFA500;
    public static final int Gold 			= 0xFFFFD700;
    //Yellow
    public static final int Yellow      	= 0xFFFFFF00;
    public static final int LightYellow 	= 0xFFFFFFE0;
    public static final int LemonChiffon 	= 0xFFFFFACD;
    public static final int LightGoldenrodYellow = 0xFFFAFAD2;
    public static final int PapayaWhip 		= 0xFFFFEFD5;
    public static final int Moccasin 		= 0xFFFFE4B5;
    public static final int PeachPuff 		= 0xFFFFDAB9;
    public static final int PaleGoldenrod 	= 0xFFEEE8AA;
    public static final int Khaki 			= 0xFFF0E68C;
    public static final int DarkKhaki 		= 0xFFBDB76B;
    //Brown
    public static final int Cornsilk 		= 0xFFFFF8DC;
    public static final int BlanchedAlmond 	= 0xFFFFEBCD;
    public static final int Bisque 			= 0xFFFFE4C4;
    public static final int NavajoWhite 	= 0xFFFFDEAD;
    public static final int Wheat 			= 0xFFF5DEB3;
    public static final int BurlyWood 		= 0xFFDEB887;
    public static final int Tan 			= 0xFFD2B48C;
    public static final int RosyBrown 		= 0xFFBC8F8F;
    public static final int SandyBrown 		= 0xFFF4A460;
    public static final int Goldenrod 		= 0xFFDAA520;
    public static final int DarkGoldenrod 	= 0xFFB8860B;
    public static final int Peru 			= 0xFFCD853F;
    public static final int Chocolate 		= 0xFFD2691E;
    public static final int SaddleBrown 	= 0xFF8B4513;
    public static final int Brown 			= 0xFFA52A2A;
    public static final int Sienna 			= 0xFFA0522D;
    public static final int Maroon 			= 0xFF800000;
    //Green
    public static final int DarkOliveGreen 	= 0xFF556B2F;
    public static final int Olive 			= 0xFF808000;
    public static final int OliveDrab 		= 0xFF6B8E23;
    public static final int YellowGreen 	= 0xFF9ACD32;
    public static final int LimeGreen 		= 0xFF32CD32;
    public static final int Lime 			= 0xFF00FF00;
    public static final int LawnGreen 		= 0xFF7CFC00;
    public static final int Chartreuse 		= 0xFF7FFF00;
    public static final int GreenYellow 	= 0xFFADFF2F;
    public static final int SpringGreen 	= 0xFF00FF7F;
    public static final int MediumSpringGreen = 0xFF00FA9A;
    public static final int LightGreen 		= 0xFF90EE90;
    public static final int PaleGreen 		= 0xFF98FB98;
    public static final int DarkSeaGreen 	= 0xFF8FBC8F;
    public static final int MediumSeaGreen 	= 0xFF3CB371;
    public static final int SeaGreen 		= 0xFF2E8B57;
    public static final int ForestGreen 	= 0xFF228B22;
    public static final int Green 			= 0xFF008000;
    public static final int DarkGreen 		= 0xFF006400;
    //Cyan
    public static final int MediumAquamarine = 0xFF66CDAA;
    public static final int Cyan 			= 0xFF00FFFF;
    public static final int Aqua 			= Cyan;
    public static final int LightCyan 		= 0xFFE0FFFF;
    public static final int PaleTurquoise 	= 0xFFAFEEEE;
    public static final int Aquamarine 		= 0xFF7FFFD4;
    public static final int Turquoise 		= 0xFF40E0D0;
    public static final int MediumTurquoise = 0xFF48D1CC;
    public static final int DarkTurquoise 	= 0xFF00CED1;
    public static final int LightSeaGreen 	= 0xFF20B2AA;
    public static final int CadetBlue 		= 0xFF5F9EA0;
    public static final int DarkCyan 		= 0xFF008B8B;
    public static final int Teal 			= 0xFF008080;
    //Blue
    public static final int LightSteelBlue 	= 0xFFB0C4DE;
    public static final int PowderBlue 		= 0xFFB0E0E6;
    public static final int LightBlue 		= 0xFFADD8E6;
    public static final int SkyBlue 		= 0xFF87CEEB;
    public static final int LightSkyBlue 	= 0xFF87CEFA;
    public static final int DeepSkyBlue 	= 0xFF00BFFF;
    public static final int DodgerBlue 		= 0xFF1E90FF;
    public static final int CornflowerBlue 	= 0xFF6495ED;
    public static final int SteelBlue 		= 0xFF4682B4;
    public static final int RoyalBlue 		= 0xFF4169E1;
    public static final int Blue        	= 0xFF0000FF;
    public static final int MediumBlue 		= 0xFF0000CD;
    public static final int DarkBlue 		= 0xFF00008B;
    public static final int Navy 			= 0xFF000080;
    public static final int MidnightBlue 	= 0xFF191970;
    //Purple
    public static final int Lavender 		= 0xFFE6E6FA;
    public static final int Thistle 		= 0xFFD8BFD8;
    public static final int Plum 			= 0xFFDDA0DD;
    public static final int Violet 			= 0xFFEE82EE;
    public static final int Orchid 			= 0xFFDA70D6;
    public static final int Magenta 		= 0xFFFF00FF;
    public static final int Fuchsia 		= Magenta;
    public static final int MediumOrchid 	= 0xFFBA55D3;
    public static final int MediumPurple 	= 0xFF9370DB;
    public static final int BlueViolet 		= 0xFF8A2BE2;
    public static final int DarkViolet 		= 0xFF9400D3;
    public static final int DarkOrchid 		= 0xFF9932CC;
    public static final int DarkMagenta 	= 0xFF8B008B;
    public static final int Purple 			= 0xFF800080;
    public static final int Indigo 			= 0xFF4B0082;
    public static final int DarkSlateBlue 	= 0xFF483D8B;
    public static final int SlateBlue 		= 0xFF6A5ACD;
    public static final int MediumSlateBlue = 0xFF7B68EE;
    //White
    public static final int White 			= 0xFFFFFFFF;
    public static final int Snow 			= 0xFFFFFAFA;
    public static final int Honeydew 		= 0xFFF0FFF0;
    public static final int MintCream 		= 0xFFF5FFFA;
    public static final int Azure 			= 0xFFF0FFFF;
    public static final int AliceBlue 		= 0xFFF0F8FF;
    public static final int GhostWhite 		= 0xFFF8F8FF;
    public static final int WhiteSmoke 		= 0xFFF5F5F5;
    public static final int Seashell 		= 0xFFFFF5EE;
    public static final int Beige 			= 0xFFF5F5DC;
    public static final int OldLace 		= 0xFFFDF5E6;
    public static final int FloralWhite 	= 0xFFFFFAF0;
    public static final int Ivory 			= 0xFFFFFFF0;
    public static final int AntiqueWhite 	= 0xFFFAEBD7;
    public static final int Linen 			= 0xFFFAF0E6;
    public static final int LavenderBlush 	= 0xFFFFF0F5;
    public static final int MistyRose 		= 0xFFFFE4E1;
    //Gray
    public static final int Gainsboro 		= 0xFFDCDCDC;
    public static final int LightGrey 		= 0xFFD3D3D3;
    public static final int Silver 			= 0xFFC0C0C0;
    public static final int DarkGray 		= 0xFFA9A9A9;
    public static final int Gray 			= 0xFF808080;
    public static final int LightSlateGray 	= 0xFF778899;
    public static final int SlateGray 		= 0xFF708090;
    public static final int DimGray 		= 0xFF696969;
    public static final int DarkSlateGray 	= 0xFF2F4F4F;
    public static final int Black 			= 0xFF000000;
}
