;?????? ?????????? FastImage ??? Xors3D (DX9)
;(c) 2006-2010 ???? ??????? MixailV aka Monster^Sage [monster-sage@mail.ru]
;??? ????????????? ?????????? ? ????? ??????? ??????????? ?????????? ?????? MixailV.
;????????? ????????? ? ?????????? FastImage.dll ?????? ?????? DLL ? ?? ????????.
;?? ?????????? ?????????? FastImage.dll ? EXE ?????.



;?????? ????????? ??????? ???????? ? ????? ? ?????????????? ????? FastImage.decls



; ?????????? ?????? ??????? ??? ??????????? ???????????? ???????? ????????
Include "inc\GetImageInfo.bb"



;?????? ????? - ????????? ??????????? ??????????
;????????? ??? ??? ??????? ? ?????

;CreateImageEx Flags (??? ???????? ????????)
Const FI_AUTOFLAGS = -1
Const FI_NONE = 0
Const FI_MIDHANDLE = 1
Const FI_FILTEREDIMAGE = 2
Const FI_FILTERED = 2

;SetBlend Flags (??? ????????????? ?????? (??????????)
Const FI_SOLIDBLEND = 0
Const FI_ALPHABLEND = 1
Const FI_LIGHTBLEND = 2
Const FI_SHADEBLEND = 3
Const FI_MASKBLEND = 4
Const FI_MASKBLEND2 = 5
Const FI_INVALPHABLEND = 6

;ImageFonts Flags
Const FI_SMOOTHFONT=1

;DrawImagePart Wrap Flags
Const FI_NOWRAP = 0
Const FI_WRAPU = 1
Const FI_MIRRORU = 2
Const FI_WRAPV = 4
Const FI_MORRORV = 8
Const FI_WRAPUV = 5
Const FI_MIRRORUV = 10

;DrawPoly consts
Const FI_POINTLIST     = 1
Const FI_LINELIST      = 2
Const FI_LINESTRIP     = 3
Const FI_TRIANGLELIST  = 4
Const FI_TRIANGLESTRIP = 5
Const FI_TRIANGLEFAN   = 6 

;	FI_POINTLIST 
;		Renders the vertices as a collection of isolated points. 
;	FI_LINELIST 
;		Renders the vertices as a list of isolated straight line segments. Calls using this primitive type fail If the count is less than 2 or is odd. 
;	FI_LINESTRIP 
;		Renders the vertices as a single polyline. Calls using this primitive type fail If the count is less than 2. 
;	FI_TRIANGLELIST 
;		Renders the specified vertices as a sequence of isolated triangles. Each group of three vertices defines a separate triangle.
;		Calls using this primitive type fail If the count is less than 3 or not evenly divisible by 3. 
;	FI_TRIANGLESTRIP 
;		Renders the vertices as a triangle strip. Calls using this primitive type fail If the count is less than 3.
;	FI_TRIANGLEFAN 
;		Renders the vertices as a triangle fan. Calls using this primitive type fail If the count is less than 3. 

Const FI_COLOROVERLAY = 1



;????? ???? ??????? ??????????
Type FI_PropertyType
	Field Blend%
	Field Alpha#, Red%, Green%, Blue%
	Field ColorVertex0%, ColorVertex1%, ColorVertex2%, ColorVertex3%                                 	
	Field Rotation#, ScaleX#, ScaleY#
	Field MatrixXX#, MatrixXY#, MatrixYX#, MatrixYY#
	Field HandleX%, HandleY%
	Field OriginX%, OriginY%
	Field AutoHandle%, AutoFlags%
	Field LineWidth#
	Field ViewportX%, ViewportY%, ViewportWidth%, ViewportHeight%
	Field MipLevel%
	Field ProjScaleX#, ProjScaleY#, ProjRotation#
	Field ProjOriginX%, ProjOriginY%
	Field ProjHandleX%, ProjHandleY%
	Field Reserved0%
	Field Reserved1%
End Type

;?????????? ?????????? ?????????? FI_Property ??? ????????? ??????????? ???????? ?? ??????
;? ????????? ???????? ???? ????? ???????  ????? ???:  GetProperty FI_Property
Global FI_Property.FI_PropertyType = New FI_PropertyType



;????? ??????? ????? ????????, ????????? ??????????? (???????? CreateImageEx)
Type FI_ImagePropertyType
	Field HandleX%
	Field HandleY%
	Field Width%
	Field Height%
	Field Frames%
	Field Flags%
	Field Texture%
	Field Reserved0%
	Field Reserved1%
End Type

;?????????? ?????????? ?????????? FI_ImageProperty ??? ????????? ??????????? ????????
;?? ?????? ??????? ????? ????????, ????????? ??????????? (???????? CreateImageEx)
;? ????????? ???????? ???? ????? ???????  ????? ???:  GetImageProperty your_image, FI_ImageProperty
Global FI_ImageProperty.FI_ImagePropertyType = New FI_ImagePropertyType



Type FI_FontPropertyType
	Field Width%
	Field Height%
	Field FirstChar%
	Field Kerning%
	Field Image%
	Field FrameWidth%
	Field FrameHeight%
	Field FrameCount%
	Field Chars[256]
End Type
Global FI_FontProperty.FI_FontPropertyType = New FI_FontPropertyType




;??? ??? ????????? ???? ?????????? ? ????????? ????? ?? ????????? (??????? TestPoint)
Type FI_TestType
	Field Result%
	Field ProjectedX%, ProjectedY%
	Field RectX%, RectY%
	Field RectU#, RectV#
	Field TextureX%, TextureY%
	Field Texture%
	Field Frame%
	Field Reserved1%
End Type
Global FI_Test.FI_TestType = New FI_TestType




;??????? ?????????????? "???????" ?????? (?????????? ??????)
;??????? ??? ??????????, ??? ?? ????????? DirectX7
;???????? src ? dest ?????? ???? ? ???????? ?? 1 ?? 10
Function xSetCustomBlend(src%, dest%)
	xSetCustomState 15,0				;DX7  SetRenderState ( D3DRENDERSTATE_AlphaTestEnable, False )
	xSetCustomState 27,1				;DX7  SetRenderState ( D3DRENDERSTATE_AlphaBlendEnable, True )
	xSetCustomState 19,src			;DX7  SetRenderState ( D3DRENDERSTATE_SrcBlend, src )
	xSetCustomState 20,dest			;DX7  SetRenderState ( D3DRENDERSTATE_DestBlend, dest )
End Function





Type FI_SurfacesType
	Field Count%
	Field Array%[256]
	Field Texture%
End Type
Global FI_Surfaces.FI_SurfacesType = New FI_SurfacesType





;??????????????? ???????, ??????????? ?? ???????? ?????? ??? ????????? ? ?????????? ??????????
Function xCreateImageEx% (texture%, width%, height%, imageFlags%=FI_AUTOFLAGS)
	If texture<>0 Then
		FI_Surfaces\Texture = texture
		FI_Surfaces\Count = xGetTextureFrames(texture)
		If FI_Surfaces\Count>0 Then
			If FI_Surfaces\Count>256 Then FI_Surfaces\Count=256
			For i=0 To FI_Surfaces\Count-1
				FI_Surfaces\Array[i] = xGetTextureSurface(texture, i)
			Next
			Return xCreateImageEx_(FI_Surfaces, width, height, imageFlags)
		EndIf
	EndIf
	Return 0
End Function

Function xLoadImageEx% (fileName$, textureFlags%=0, imageFlags%=FI_AUTOFLAGS)
	If ImageInfo_ReadFile (fileName) Then
		Return xCreateImageEx ( xLoadTexture (fileName, textureFlags), ImageInfo_Width, ImageInfo_Height, imageFlags)
	EndIf
	Return 0
End Function

Function xLoadAnimImageEx% ( fileName$, textureFlags%, frameWidth%, frameHeight%, firstFrame%, frameCount%, imageFlags%=FI_AUTOFLAGS )
	textureFlags = (textureFlags And $3F) Or $9
	DebugLog textureFlags 
	Return xCreateImageEx ( xLoadAnimTexture (fileName, 1+2 , frameWidth, frameHeight, firstFrame, frameCount), frameWidth, frameHeight, imageFlags)
End Function

Function xDrawImageEx% (image%, x%, y%, frame%=0)
	Return xDrawImageEx_(image, x, y, frame)
End Function

Function xDrawImageRectEx% (image%, x%, y%, width%, height%, frame%=0)
	Return xDrawImageRectEx_(image, x, y, width, height, frame)
End Function

Function xDrawImagePart% (image%, x%, y%, width%, height%, partX%=0, partY%=0, partWidth%=0, partHeight%=0, frame%=0, wrap%=FI_NOWRAP)
	Return xDrawImagePart_(image, x, y, width, height, partX, partY, partWidth, partHeight, frame, wrap)
End Function

Function xDrawPoly% (x%, y%, bank%, image%=0, frame%=0, color%=FI_NONE)
	Return xDrawPoly_(x, y, bank, image, frame, Color)
End Function

Function xDrawRect% (x%, y%, width%, height%, fill%=1)
	xDrawRect_ x, y, width, height, fill
End Function

Function xDrawRectSimple% (x%, y%, width%, height%, fill%=1)
	xDrawRectSimple_ x, y, width, height, fill
End Function

Function xLoadImageFont% (filename$, flags%=FI_SMOOTHFONT)
	Local f, i, l$, r$, AnimTexture$, AnimTextureFlags, Texture

	filename=Replace (filename,"/", "\")
	f = ReadFile(filename)
	If f=0 Then Return 0

	FI_FontProperty\Width=0
	FI_FontProperty\Height=0
	FI_FontProperty\FirstChar=0
	FI_FontProperty\Kerning=0
	FI_FontProperty\Image=0
	FI_FontProperty\FrameWidth=0
	FI_FontProperty\FrameHeight=0
	FI_FontProperty\FrameCount=0
	For i=0 To 255
		FI_FontProperty\Chars[i]=0
	Next
	AnimTextureFlags=4

	While Not Eof(f) 
		l=Trim(ReadLine(f))
		i=Instr(l,"=",1)
		If Len(l)>0 And Left(l,1)<>";" And i>0 Then
			r=Trim(Right(l,Len(l)-i))
			l=Upper(Trim(Left(l,i-1)))
			Select l
				Case "ANIMTEXTURE"
					AnimTexture=r
				Case "ANIMTEXTUREFLAGS"
					AnimTextureFlags=Int(r)
				Case "FRAMEWIDTH"
					FI_FontProperty\FrameWidth=Int(r)
				Case "FRAMEHEIGHT"
					FI_FontProperty\FrameHeight=Int(r)
				Case "FRAMECOUNT"
					FI_FontProperty\FrameCount=Int(r)
				Case "WIDTH"
					FI_FontProperty\Width=Int(r)
				Case "HEIGHT"
					FI_FontProperty\Height=Int(r)
				Case "FIRSTCHAR"
					FI_FontProperty\FirstChar=Int(r)
				Case "KERNING"
					FI_FontProperty\Kerning=Int(r)				
				Default
					If Int(l)>=0 And Int(l)<=255 Then
						FI_FontProperty\Chars[Int(l)]=Int(r)
					EndIf
			End Select
		EndIf
	Wend
	CloseFile f

	If Len(AnimTexture)>0 And FI_FontProperty\FrameWidth>0 And FI_FontProperty\FrameHeight>0 And FI_FontProperty\FrameCount>0 Then
		f=1
		Repeat
			i=Instr(filename,"\",f)
			If i<>0 Then f=i+1
		Until i=0
		If flags=FI_SMOOTHFONT Then   :   flags=FI_FILTEREDIMAGE   :   Else   :   flags=FI_NONE   :   EndIf
		FI_FontProperty\Image = xLoadImageEx ( Left(filename,f-1)+AnimTexture, (AnimTextureFlags And $6) Or $39, flags)
		Return xCreateImageFont( FI_FontProperty )
	EndIf
	Return 0
End Function

Function xStringWidthEx% (txt$, maxWidth%=10000)
	Return xStringWidthEx_(txt, maxWidth)
End Function

Function xDrawText% (txt$, x%, y%, centerX%=0, centerY%=0, maxWidth%=10000)
	Return xDrawText_(txt$, x, y, centerX, centerY, maxWidth)
End Function

Function xDrawTextRect% (txt$, x%, y%, w%, h%, centerX%=0, centerY%=0, lineSpacing%=0)
	Return xDrawTextRect_(txt, x, y, w, h, centerX, centerY, lineSpacing)
End Function



Function xInitDraw% (def=0)
	Return xInitDraw_ ( xGetDevice(), 0 )
End Function

Function xGetProperty% ()
	Return xGetProperty_ (FI_Property)
End Function

Function xGetImageProperty% (image%)
	Return xGetImageProperty_ (image, FI_ImageProperty)
End Function

Function xGetFontProperty% (font%)
	Return xGetFontProperty_ (font, FI_FontProperty)
End Function



Function xTestRect% (xPoint%, yPoint%, xRect%, yRect%, WidthRect%, HeightRect%, Loc%=0)
	Return xTestRect_ (xPoint, yPoint, xRect, yRect, WidthRect, HeightRect, Loc, FI_Test, 1)
End Function

Function xTestOval% (xPoint%, yPoint%, xOval%, yOval%, WidthOval%, HeightOval%, Loc%=0)
	Return xTestOval_ (xPoint, yPoint, xOval, yOval, WidthOval, HeightOval, Loc, FI_Test, 1)
End Function

Function xTestImage% (xPoint%, yPoint%, xImage%, yImage%, Image%, alphaLevel%=0, Frame%=0, Loc%=0)
	If xTestImage_ (xPoint, yPoint, xImage, yImage, Image, Loc, FI_Test, 1) And alphaLevel>0 And FI_Test\Texture<>0 Then
		If ( xReadPixel( FI_Test\TextureX, FI_Test\TextureY, xTextureBuffer(FI_Test\Texture,Frame) ) Shr 24 ) < alphaLevel Then FI_Test\Result = 0
	EndIf
	Return FI_Test\Result
End Function

Function xTestRendered% (xPoint%, yPoint%, alphaLevel%=0, Loc%=0)
	If xTestRendered_ (xPoint, yPoint, Loc, FI_Test, 1) And alphaLevel>0 And FI_Test\Texture<>0 Then
		If ( xReadPixel( FI_Test\TextureX, FI_Test\TextureY, xTextureBuffer(FI_Test\Texture,FI_Test\Frame) ) Shr 24 ) < alphaLevel Then FI_Test\Result = 0
	EndIf
	Return FI_Test\Result
End Function

Function xFreeImageEx% (image%, freeTexture%=0)
	;If FreeTexture<>0 And GetImageProperty(image)<>0 And FI_ImageProperty\Texture<>0 Then xFreeTexture FI_ImageProperty\Texture
	xFreeImageEx_ image
End Function

Function xFreeImageFont% (font%)
	If xGetFontProperty(font)<>0 And FI_FontProperty\Image<>0 Then
		If xGetImageProperty(FI_FontProperty\Image)<>0 And FI_ImageProperty\Texture<>0 Then xFreeTexture FI_ImageProperty\Texture
	EndIf
	xFreeImageFont_ font
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D