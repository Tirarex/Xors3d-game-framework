;Patchs
Global cudir$=CurrentDir$() 
Global GlobalDirectoru$=""
Global GameFolder$="DefaultData"+"\"
Global camera ,BodyPos
Global w,h,Wire_Mode
Global TexturesPatch$=GlobalDirectoru$+GameFolder$+"textures\"
Global PlayerModelPatch$=GlobalDirectoru$+GameFolder$+"models\"
Global ShaderPatch$=GlobalDirectoru$+GameFolder$+"shaders\"
Global SoundPatch$=GlobalDirectoru$+GameFolder$+"Sounds\"
Global FontPatch$=GlobalDirectoru$+GameFolder$+"fonts\"
Global MapsPatch$=GlobalDirectoru$+GameFolder$+"map\"
Global LangPatch$=GlobalDirectoru$+GameFolder$+"Langulage\"
Global DataPatch$=GlobalDirectoru$+GameFolder$
Global NoEditorLights=1




;Game
Global gameFPS = 60 
Global framePeriod = 1000 / gameFPS 
Global frameTime = MilliSecs () - framePeriod
Global frameTween#

Global PhysicTime
Global DeferredTime 
Global Qualitypreset=1 ;0-low 1-high



;SaveLoadSystem 

Type Locale
	Field LoID
	Field tx$
End Type



Function LoadLocal(LoANg$)
	o=ReadFile("DefaultData\Langulage\"+LoANg$+".txt")
	While Not Eof(o)
		t.Locale=New Locale
		t\LoID=ReadLine(o)
		t\tx$=ReadLine(o)
		ReadLine(o)
	Wend
	CloseFile(o)
End Function



Function Getletter$(id)
	For L.Locale = Each Locale
		If L\LoID=id
			Return L\tx$
		EndIf
	Next 
End Function

Type bullet
	Field Mesh
	Field body
	Field helath
	Field light
	
	Field magnit
	Field Timer 
	
	
	
	
End Type




	
	
Function Create_bulletd(ent)
	Pr.bullet= New bullet
	Pr\Mesh=xCreateCube()
	xScaleEntity Pr\Mesh,0.1,0.1,0.1
	;xEntityTexture Pr\Mesh, CreateCheckerTexture()
	Pr\helath=20000000000
	Pr\body = BodyCreatehull(Pr\Mesh,10) 
	pxBodySetPosition Pr\body,xEntityX(ent),xEntityY(ent)+20,xEntityZ(ent)
	pxBodySetRotation pr\body,xEntityPitch(ent),xEntityYaw(ent),xEntityRoll(ent)
	pxBodySetMyForce(pr\body,0,0,250)
	pr\light= CreateDeferredLight(1,Pr\Mesh)
	DeferredLightColor(pr\light,Rand(1,255),Rand(1,255),Rand(1,255))
	DeferredLightRange(pr\light,5)
	;SetDeferredMesh( pr\light)
	;AddDeferredReciver(pr\Mesh)
	
	pr\Timer=100
	pr\magnit = pxCreateMagnet(1, 0, -2000)
	pxMagnetSetMaxRadius(pr\magnit, 85)
	
End Function

Function Update_bullets()
	For Pr.bullet= Each bullet
		Pr\helath=Pr\helath-1
		xBodySetEntity pr\mesh,pr\body
		
		
		pxMagnetSetPosition(pr\magnit, xEntityX(pr\mesh),xEntityY(pr\mesh)+1,xEntityZ(pr\mesh))
		
		
		If xKeyHit(Key_R)
			Pr\helath=0
			For it=0 To 10
				p.Particles = CreateParticle(xEntityX(pr\mesh, True)+Rand(-30,30), xEntityY(pr\mesh, True)+Rand(-10,10), xEntityZ(pr\mesh, True)+Rand(-30,30), 0, Rand(10,40), -0.2, 1)
				p\Achange = 1
				BreakBodyInRad(85,pr\mesh)
			Next
		pxMagnetActivate(pr\magnit, 0, 1)
	EndIf 
			
		If Pr\helath=0
			DeleteDeferredLight(pr\light)
			pxDeleteBody pr\body
			If pr\magnit<>0
				;pxDeleteBody pr\magnit
				
			EndIf 
				xFreeEntity pr\mesh
				Delete pr
			EndIf
		Next 
End Function






Function Loadsettings()
	INI_OpenFile("Config.ini")
	w = INI_ReadValue("game", "w", "800") 
	h = INI_ReadValue("game", "h", "600") 
	fullS= INI_ReadValue("game", "fullS", "0") 
	vsunc = INI_ReadValue("game", "vsunc", "0")
	joy =     INI_ReadValue("game","Joy",    "0") 
	ShadowMode=INI_ReadValue("game","ShadowMode", "0")
	
	SetupShadows(ShadowMode)
	
	OldTextureSize=INI_ReadValue("game","SSLRTexSize", "512")
	PostSSLR=INI_ReadValue("game","PostSSLR", "0")  
	PostFXAA=INI_ReadValue("game","PostFXAA", "0") 
	PostFXAALevels=INI_ReadValue("game","PostFXAALevels", "1") 
	PostSSAO=INI_ReadValue("game","PostSSAO", "0") 
	DeferredParalax=INI_ReadValue("game","Paralax", "0")
	EnableParticleLighting=INI_ReadValue("game","ParticleLighting", "0") 
	PostProcessing=INI_ReadValue("game","PostProcessing", "0")
	INI_CloseFile%()
	For k=1 To CountGfxModes3D()
		If w=GfxModeWidth(k)
			If h=GfxModeHeight(k)
				GfxSelected=k
			EndIf 
		EndIf 
	Next
End Function


Function SaveSettings()
	INI_OpenFile("Config.ini")
	INI_WriteValue("game","w",w)	
	INI_WriteValue("game","h",h)
	INI_WriteValue("game","fullS",fullS)
	INI_WriteValue("game","vsunc",vsunc)
	INI_WriteValue("game","ShadowMode",ShadowMode)
	INI_WriteValue("game","PostSSLR",PostSSLR)
	INI_WriteValue("game","SSLRTexSize",OldTextureSize)
	INI_WriteValue("game","PostFXAA",PostFXAA)
	INI_WriteValue("game","PostFXAALevels",PostFXAALevels)
	INI_WriteValue("game","Paralax",DeferredParalax)
	INI_WriteValue("game","ParticleLighting",EnableParticleLighting)
	INI_WriteValue("game","PostProcessing",PostProcessing)
	
	INI_CloseFile%()
End Function

Global vsunc,debugmode,fullS

Function pxBodyMoveToPoint(body,posx#,posy#,posz#,modeForce=2,speedx#=20,speedy#=20,speedz#=20,accuracy#=0,maxforce#=0)
	bPosX#=pxBodyGetPositionX(body)
	bPosY#=pxBodyGetPositionY(body)
	bPosZ#=pxBodyGetPositionZ(body)
	
	If bPosX>posx-accuracy Or bPosX<posx+accuracy Then			
		forceX#=posx-bPosX
	Else	
		forceX#=0	
	EndIf
	
	If bPosY>posy-accuracy Or bPosY<posy+accuracy  Then
		forceY#=posy-bPosY
	Else	
		forceY#=0
	EndIf
	
	If bPosZ>posy-accuracy Or bPosZ<posy+accuracy Then
		forceZ#=posz-bPosZ
	Else	
		forceZ#=0	
	EndIf
	
	If maxforce<>0 Then
		If forceX>maxforce Then
			forceX=maxforce
		Else
			If forceX<-maxforce Then
				forceX=-maxforce
			EndIf	
		EndIf
		If forceY>maxforce Then
			forceY=maxforce
		Else
			If forceY<-maxforce Then
				forceY=-maxforce
			EndIf	
		EndIf
		If forceZ>maxforce Then
			forceZ=maxforce
		Else
			If forceZ<-maxforce Then
				forceZ=-maxforce
			EndIf	
		EndIf
	EndIf
	
	pxBodyAddForce(body,forceX*speedx,forceY*speedy,forceZ*speedz,modeForce)
	
	If Int(forceX)=0 And Int(forceY)=0 And Int(forceZ)=0
		Return True
	Else
		Return False	
	EndIf
	
End Function
Global SmokeTex
;GameFunc
Function drawpivot(e,camera,length)
	
	
	
	
	;Get initial position
	xCameraProject camera,xEntityX(e,1),xEntityY(e,1),xEntityZ(e,1)
	x=xProjectedX()
	y=xProjectedY()
	
	;Draw X axis
	xSetColor 255,0,0
	xMoveEntity e,length,0,0
	xCameraProject camera,xEntityX(e,1),xEntityY(e,1),xEntityZ(e,1)
	xDrawLine x,y,xProjectedX(),xProjectedY()
	xMoveEntity e,-length,0,0
	
	;Draw Y axis
	xSetColor 0,255,0
	xMoveEntity e,0,length,0
	xCameraProject camera,xEntityX(e,1),xEntityY(e,1),xEntityZ(e,1)
	xDrawLine x,y,xProjectedX(),xProjectedY()
	xMoveEntity e,0,-length,0
	
	;Draw Z axis
	xSetColor 0,0,255
	xMoveEntity e,0,0,length
	xCameraProject camera,xEntityX(e,1),xEntityY(e,1),xEntityZ(e,1)
	xDrawLine x,y,xProjectedX(),xProjectedY()
	xMoveEntity e,0,0,-length
End Function

Function LoadStandarts()
	
	xKey ("MT551-bG5a7-69f6w-MB4pP-3l2Z5")
	xSetEngineSetting("Splash::TilingTime",0)
	xSetEngineSetting("Splash::AfterTilingTime",0)
	xAppTitle "game"
	If EditorEngine=0
		xGraphics3D w, h, 32, fullS, vsunc
	Else
		w =1024
		h =538
		xGraphics3D 1050, 490, 32, 0, vsunc
	EndIf 
	xInitalizeSound	(		)	
	
	LoadLocal("En_eu")
	xInitDraw
	xCreateDSS(4096,4096)
	pxCreateWorld(0, ">j58ma6a[m5a\\08]m4eUl54Tm3ce\6a")
	pxSetGravity(0,-30,0)
	camera=xCreateCamera()
	xCameraRange camera,0.5,5000
	xCreateListener(camera,0.05)
	ray = pxCreateRay%()
	pxBodySetCollisionGroupFlag(1, 2, 0)
	
	BodyPos=xCreatePivot()
	xPositionEntity BodyPos,0.5,0,20
	xEntityParent BodyPos,camera,1
	
	SmokeTex=xLoadTexture(TexturesPatch$+"smoke.png",2)
	
	
End Function

Function CurveValue#(newvalue#, oldvalue#, increments)
	If increments >  1 Then oldvalue# = oldvalue# - (oldvalue# - newvalue#) / increments 
	If increments <= 1 Then oldvalue# = newvalue# 
	Return oldvalue# 
End Function

Function numval(nm#)
	number#=0.025*nm
	Return number# 
End Function

;Keys
Global xkht57,xkht30,xkht32,xkht31,xkht17,xkht19 ,msh1,msh2,wpick,hplus#,msd1,fs,xkht42,xkht29,msz,xkht18
Global msd2
Global joy
Global mouseht

Global mouseSpeedX#,mouseSpeedY#
Global Mh1,Mh2,Md1,Md2,mX,mY
Global SelectedWindowId=1

Function Updateipunt()
	;msh1=xMouseHit(1)
	msh2=xMouseHit(2)
	msd1=xMouseDown(1)
	msd2=xMouseDown(2)
	msz=xMouseZSpeed ()
	
	
	Mh1=msh1
	Mh2=msh2
	Md1=msd1
	Md2=msd2
	
	mouseSpeedX = xMouseXSpeed()
	mouseSpeedY = xMouseYSpeed()
	
	
	mX=xMouseX()
	mY=xMouseY()
	
		dirx=xJoyXDir()
		If dirx=-1
		    xkht30=1
		EndIf
		If dirx=1
		    xkht32=1
		EndIf		
		
		dirx=xJoyYDir()
		If dirx=1
		    xkht31=1
		EndIf
		If dirx=-1
		    xkht17=1
		EndIf		
		
		If xJoyDown(5)
			xkht42=1
		EndIf
		
		If xJoyHit(6)
			xkht57=1
		EndIf			
		
		
		If xJoyDown(7)
			xkht29=1
		EndIf		
		
		
		xkht30=xKeyDown(30)
		xkht32=xKeyDown(32)
		xkht31=xKeyDown(31)
		xkht17=xKeyDown(17)
		xkht42=xKeyDown(42)
		xkht57=xKeyHit(57)
		xkht29=xKeyDown(29)
		xkht18=xKeyHit(18)
		xkht19=xKeyHit(19)
		
	
End Function

Function nullkeys()
	xkht29=0
	xkht57=0
	xkht32=0
	xkht30=0
	xkht31=0
	xkht17=0
	xkht42=0
	xkht18=0
	msh1=0
	msh2=0
End Function

Global Gamelogo,Arrow,Pointer
Global ConsoleFont,MenuFont


Global RainTexture
Function LoadUI()
	
	Gamelogo=xLoadImageEx(TexturesPatch$+"GameLogo.png", 1+2)
	Pointer=xLoadImageEx(TexturesPatch$+"pointer.png", 1+2, FI_AUTOFLAGS)
	Arrow=xLoadImageEx(TexturesPatch$+"Arrow.png", 1+2, FI_AUTOFLAGS)
	RainTexture=xLoadImageEx(TexturesPatch$+"droplet_nm.dds", 1+2, FI_AUTOFLAGS)
	Arrow=xCreateImageEx% (xLoadTexture(TexturesPatch$+"Arrow.png"), 32, 32)
	MenuFont=xLoadImageFont(FontPatch$+"impact_30.txt")
	ConsoleFont=xLoadImageFont(FontPatch$+"T2.txt")
	
End Function

Type Drop
	Field Scale#
	Field size#
	Field x#, y#
	Field lifeTime#
End Type


Function CreateDroplet()
	d.Drop = New Drop
	d\lifeTime=1
	d\x = Rand(0,ScreenW)
	d\y = Rand(0,ScreenH)
	d\Scale#=Rnd(0.1,0.6)
End Function

Function UpdateDroplets()
	xSetBuffer(xTextureBuffer(DropletBuff))
	xClsColor(128, 128, 0)
	xCls()
	xStartDraw
	FIDef()
	For d.Drop = Each Drop
		d\lifeTime#=d\lifeTime#-0.01
		d\y#=d\y#+d\Scale#
		
		xSetAlpha d\lifeTime#
		xSetScale(d\Scale#,d\Scale#)
		xDrawImageEx RainTexture,d\x#, d\y#
		
		If d\lifeTime%<0
			Delete d
		EndIf
	Next
	xEndDraw
	xSetBuffer(xBackBuffer())
End Function

Function CreateDropletBuffer()
Local buffer% = xCreateTexture(xGraphicsWidth(), xGraphicsHeight(), FLAGS_COLOR + 16384+ FLAGS_ALPHA); + FLAGS_ALPHA)
xSetBuffer(xTextureBuffer(buffer))
xClsColor(128, 128, 0)
xCls()
xSetBuffer(xBackBuffer())
Return buffer
End Function

Global Debug_Normals,Debug_Albedo,UpdateReflectionsFunction LoadGame(mapdmap$="DevTest")
	UNLoadMenuBackuground()
	
	createplayer()
	LoadMapDP(mapdmap$+"\","ololo");Chapter_2
	;DevTest
	
	
	ambient =CreateDeferredLight(5,camera)
;DeferredLightColor(ambient,1,1,2)
	DeferredLightColor(ambient,5,5,10)
	
	UpdateDeferredShadows()
	UpdateLightEnt()
	pxRenderPhysic(30,0)
	UpdatePhysXObj()
	pxRenderPhysic(30,0)
	RenderWorldDeferred()
	UpdateDeferredShadows()
	
	UpdateReflections=0
	
	UpdateAllCubeRef()
	;xFreeEntity NOBAGCUBESUKA
	
End Function

For i=1 To Len(gNameTexture)
	Char$=Right (gNameTexture,i)
	Char=Left(Char,1)
	If Char="\" Then					
		gNameTexture=Right(gNameTexture,i-1)
	EndIf		
Next	
FreeTexture SurfPickTexture


Function HideWeapon()
	If WeaponShow=1
		WeaponShowAnim=1
		xAnimate HeadWeapon,3,-2,1
		AmmoCount=800
		xEmitSound( DrawSound,camera)
		HideTimer=25
	EndIf 
End Function
Function ShowWeapon()
	WeaponShowAnim=1
	xAnimate HeadWeapon,3,2,1
	AmmoCount=8
	
	xEmitSound( DrawSound,camera)
	xPositionEntity HeadWeapon,0.3,0,-1.2
	
End Function


Function Shooot(Ent,Razb#=0.05)
	
	pxRaySetPosition(ray, xEntityX(Ent,1), xEntityY(Ent,1),xEntityZ(Ent,1))
	xTFormVector 0,0,1,Ent, 0
	DirX# = xTFormedX()+Rnd(-Razb#,Razb#);
	DirY# = xTFormedY()+Rnd(-Razb#,Razb#)
	DirZ# = xTFormedZ()+Rnd(-Razb#,Razb#)
	pxRaySetDir(ray,DirX,DirY,DirZ)
	
	value_x#=pxRayGetPickX(ray,0)
	value_y#=pxRayGetPickY(ray,0)
	value_z#=pxRayGetPickZ(ray,0)
	
	value_nx#=pxRayGetPickNX(ray,0)
	value_ny#=pxRayGetPickNY(ray,0)
	value_nz#=pxRayGetPickNZ(ray,0)
	
	PickedBody = pxRayGetBody(ray,1)
	
	If PickedBody<>0
		pxBodyAddForceAtPos PickedBody,DirX#*30,DirY#*30,DirZ#*30, 0, 0,0,1	
		BreakBody(PickedBody)
	Else 
		
		fg=xLoadMesh(PlayerModelPatch$+"hole.3ds")
		xScaleEntity  fg,20,20,20
		holetex=xLoadAnimTexture(TexturesPatch$+"bullethole.png",4,64,64,0,32)
		
		holetex_p=xLoadAnimTexture(TexturesPatch$+"bullethole_p.png",4,64,64,0,32)
		
		xEntityTexture fg,holetex,2,0
		AddDeferredReciver(fg)
		xEntityTexture fg,holetex_p,2,2
		
		xAlignToVector fg,value_nx#,value_ny#,value_nz#,2,2
		xPositionEntity 	fg,value_x#,value_y#,value_z#
		xTurnEntity(fg,0,Rand(360),0) 
		xMoveEntity fg,0,0.1,0,0
	EndIf
	
	
	
	
	For df=0 To 10
		p.Particles = CreateParticle(value_x#,value_y#,value_z#, 0, 0.5, 4, 1)
		p\speed = 0.3
		xEntityTexture(p\obj,DebrisTex)
		xAlignToVector p\pvt,value_nx#,value_ny#,value_nz#,2,2
		xTurnEntity(p\pvt, Rnd(-55, 55), Rnd(-55, 55), 0)
		p\SizeChange = 0
		p\Achange = 0.05
	Next 
End Function 



Function UnLoadGame()
	DeleteDeferredRecivers()
	FreeDeferredLights()
	freeplayer()
	FreeMap()
	DeleteAllPhysXObj()
	DeleteAllScattering()
	DeleteAllAlphaEntity()
	DeleteMapSounds()
	DeleteAllCubeRef()
	For L.LightEntity = Each LightEntity
		Delete L
	Next
	
	xRotateEntity camera,0,0,0
	xPositionEntity camera,0,0,0
	LoadMenuBackuground()
	NOBAGCUBESUKA=xCreatePivot()
	
End Function

Global StatusBarPercent=0


Function Status_bar(percent=1)
	;xRenderWorld
	;xCls
	bar=3
	StatusBarPercent=StatusBarPercent+percent
	box_x=(xGraphicsWidth()/2)-(bar*100)/2
	box_y=xGraphicsHeight()/2	
	xRect box_x,box_y,bar*StatusBarPercent,10,1
	xRect box_x-2,box_y-2,bar*100+4,10+4,0
	xFlip
End Function

Global HelpTimer

Function FIDrawHelp()
	If HelpTimer>0
		HelpTimer=HelpTimer-1
	EndIf
 	If HelpTimer>0
		xDrawImageEx Arrow,w/2-32-100,h-64
		xSetRotation(180)
		xDrawImageEx Arrow,w/2+32-100,h-64
		xSetRotation(90)
		xDrawImageEx Arrow,w/2-100,h-96
		xSetRotation(-90)
		xDrawImageEx Arrow,w/2-100,h-64
	EndIf 
End Function


Function FIDef()
	xSetRotation(0)
	xSetColor(255, 255, 255)
	xSetAlpha 1
	xSetBlend FI_ALPHABLEND
	xSetScale(1,1)
	xSetImageFont ConsoleFont
End Function


Function DrawDebug(Id)
	
	DrawDTime=MilliSecs ()
	
	
	xDrawText "FPS:"+xGetFPS(),10,10
	xDrawText "DIP:"+xDIPCounter(),10,20
	xDrawText "TRIS:"+xTrisRendered(),10,30
	
	xDrawText "PhysicTime:"+PhysicTime,10,40
	
	
	xDrawText "DeferredTime:"+DeferredTime,10,60
	xDrawText "RenderTime:"+RenderTime,10,70
	xDrawText "ShadowsTime:"+ShadowsTime,10,80
	xDrawText "LightParamsTime:"+LightParamsTime,10,90
	xDrawText "ReflectionTime:"+ReflectionTime,10,100
	xDrawText "RenderLightTime:"+RenderLightTime,10,110
	xDrawText "PostTime:"+PostTime,10,120
	
	;xDrawText "Body:"+GetBodyName(Id),90,50
	
	xDrawImageEx Debug_Normals,64,64+50
	xDrawImageEx Debug_Albedo,64,64+128+50
	
	
	pxcount=0
	For PX.PhysXSystem = Each PhysXSystem
		xCameraProject camera,xEntityX(PX\ObjMesh),xEntityY(PX\ObjMesh),xEntityZ(PX\ObjMesh)
	;	If xEntityInView(PX\ObjMesh,camera)  
			xSetColor(255, 255, 255)
			xSetScale(1,1)
			xDrawText "Body",xProjectedX(),xProjectedY()-10
			xDrawText "Name:"+PX\ObjName$,xProjectedX(),xProjectedY()-20
			xDrawText "Mass:"+PX\ObjMass,xProjectedX(),xProjectedY()-30
			xDrawText "Material:"+PX\ObjMaterial,xProjectedX(),xProjectedY()-40
	;	EndIf 
		pxcount=pxcount+1
	Next
	
	AllLightsCount=0
	For l.DeferredLight= Each DeferredLight
		AllLightsCount=AllLightsCount+1
		If xEntityInView(l\CullMesh,camera) 
			drawpivot(l\CullMesh,camera,5)
			xSetColor(255, 255, 255)
			xDrawText "Light",xProjectedX(),xProjectedY()-10
			xDrawText "Range:"+l\Radius,xProjectedX(),xProjectedY()-20
			xDrawText "Type:"+l\LightType,xProjectedX(),xProjectedY()-30
		EndIf 
	Next	
	
	
	xDrawText "PhysOj:"+pxcount,10,170
	xDrawText "Lights:"+AllLightsCount,10,180
	xDrawText "Camera:"+"X"+xEntityX(camera)+";Y"+xEntityY(camera)+";Z"+xEntityZ(camera),10,190	
	
	Local x= 5
	Local y= h-70
	
	Local TempY% = y + 40
	Local cm.ChatMessage
	
	For cm.ChatMessage = Each ChatMessage
		If TempY < y - 160 Then
			Delete cm
		Else
			Select cm\tcolor$ 
				Case "white"
					xSetColor(255, 255, 255)
				Case "red"
					xSetColor(255, 0, 0)
				Case "blue"
					xSetColor(0, 0, 255)
				Case "green"
					xSetColor(0, 255, 0)
			End Select
			
			
			xDrawText(cm\MessageText,x + 3, TempY)
			TempY = TempY - 15
		EndIf
	Next
	DrawDTime=MilliSecs ()-DrawDTime
	
	xDrawText "DrawDtime:"+DrawDTime,10,200
	
End Function




Function ChatText(txt$,tcolor$="white")
	Local c.ChatMessage = New ChatMessage
	Insert c Before First ChatMessage
	c\MessageText = txt
	c\tcolor$=tcolor$
End Function

Type ChatMessage
	Field MessageText$
	Field tcolor$
End Type

Global deb,fly
Function Draw()
	xStartDraw
	FIDef()
	If xKeyHit(KEY_F11) 
		Wire_Mode=1-Wire_Mode
	EndIf
	If xKeyHit(KEY_F12) 
		deb=1-deb
	EndIf
	
	xSetAlpha HandspointerAlpha#
	xSetScale(0.8,0.8)
	xDrawImageEx Pointer,w/2,h/2
	FIDef()
	If deb=1
		FIDef()
		DrawDebug(z_id)
	EndIf 
	FIDrawHelp()
	FIDef()
	updatemenu()
	xEndDraw
End Function


Function UpdateLogic()
	
	If xKeyHit(KEY_Z) fly=1-fly
	If HandspointerAlpha#<0
		HandspointerAlpha#=0
	EndIf
	If HandspointerAlpha#>1
		HandspointerAlpha#=1
	EndIf	
	
	
	If ShowHands=1
		HandspointerAlpha#=HandspointerAlpha#+0.09
	Else 
		HandspointerAlpha#=HandspointerAlpha#-0.06
	EndIf 
	
	If xKeyHit(38) 
		FlashLightEnable=1-FlashLightEnable 
	EndIf
	DeferredLightState(FlashLight,FlashLightEnable)
	
	If fly=1
		UpdateFlyCam()
	Else
		updateplayer(camera)
	EndIf 
	
End Function


Function EntityMoveToPoint(entity,posx#,posy#,posz#,speedx#=0.02,speedy#=0.02,speedz#=0.02, trans_use=0, acc#=0.4)
	
	eposx#=EntityX(entity,1)
	eposy#=EntityY(entity,1)
	ePosZ#=EntityZ(entity,1)
	
	If ePosX+acc>posx Or ePosX-acc<posx Then			
		forceX#=posx-ePosX
	Else	
		forceX#=0	
	EndIf
	
	If ePosY>posy-acc Or ePosY<posy+acc Then
		forceY#=posy-ePosY
	Else	
		forceY#=0
	EndIf
	
	If ePosZ>posy-acc Or ePosZ<posy+acc Then
		forceZ#=posz-ePosZ
	Else	
		forceZ#=0	
	EndIf
	
	If trans_use=0
		MoveEntity entity,forceX*speedx,forceY*speedy,forceZ*speedz
	Else
		TranslateEntity entity,forceX*speedx,forceY*speedy,forceZ*speedz
	EndIf
	
	If Int(forceX)=0 And Int(forceY)=0 And Int(forceZ)=0
		Return True
	Else
		Return False	
	EndIf
	
End Function

Function Distance2#( x#, y#, z#, x2#, y2#, z2# )
	;distance function a very useful function indeed.
	
	value#=Sqr((x#-x2#)*(x#-x2#)+(y#-y2#)*(y#-y2#)+(z#-z2#)*(z#-z2#))
	
	Return value#
End Function



































Type Window
	Field Title$
	Field X
	Field Y
	Field Width
	Field Height
	Field MoweWindow
	Field id 
End Type
Type WindowText
	Field Text$
	Field X
	Field Y
	Field id 
	Field tid
End Type
Type WindowSelc
	Field Text$
	Field X
	Field Y
	Field id 
	Field tid
	Field State
End Type

Function AddSelectorToWindow.WindowSelc(WText$,x,y,id,sstid)
	ws.WindowSelc=New WindowSelc
	ws\Text$=WText$
	ws\x=x
	ws\y=y
	ws\id =id
	ws\tid =sstid
	Return ws
End Function

Function SetSState(sdID,GValue)
	For ws.WindowSelc = Each WindowSelc
		If ws\tid=sdID
			ws\State=GValue
		EndIf
	Next    
End Function

Function GetSState(sdID)
	For ws.WindowSelc = Each WindowSelc
		If ws\tid=sdID
			Return ws\State
		EndIf
	Next    
End Function

Type WindowButton
	Field Text$
	Field X
	Field Y
	Field id 
	Field Bid
	Field Width
	Field Height
	Field state
	
	Field OnlyHit
End Type
Type Windows
	Field id 
End Type

Type WindowsSlider
	Field id
	Field sid
	Field label$
	Field x
	Field y
	Field xs
	Field value#
	Field Multipler
	Field showlabel
	Field held
End Type

Function AddButtonToWindow(WText$,x,y,id,bid,OnlyHit=0)
	wb.WindowButton=New WindowButton
	wb\Text$=WText$
    wb\x=x
	wb\y=y
	wb\id =id
	wb\bid=bid
	wb\state=0
	wb\Width=6+xStringWidth(wb\Text$)
	wb\Height=6+xStringHeight(	wb\Text$)
	wb\OnlyHit=OnlyHit
End Function


Function NewSlider(id,sid,x,y,xs,value#=0,Multipler,label$)
	n.WindowsSlider = New WindowsSlider
	n\id  = id 
	n\label$=label$
	n\sid  = sid
	n\x = x
	n\y = y
	n\xs = xs
	n\Multipler=Multipler
	n\value# = value#
	n\held = False
End Function


Function GetValue#(sID)
	For n.WindowsSlider = Each WindowsSlider
		If n\sid=sID
			Return  Float(n\value)*Float(n\Multipler)           
		EndIf
	Next    
End Function


Function SetValue#(sdID,GValue#)
	For n.WindowsSlider = Each WindowsSlider
		If n\sid=sdID
			n\value# =  Float(GValue)/ Float(n\Multipler)  
		EndIf
	Next    
End Function




Function GetButtonState(id)
	For wb.WindowButton=Each WindowButton
		If wb\bid=id
			tempstate=wb\state	
			wb\state=0	
			Return tempstate
		EndIf 
	Next 
End Function
Function AddTextToWindow(WText$,x,y,id,textid)
	wt.WindowText=New WindowText
	wt\Text$=WText$
	wt\x=x
	wt\y=y
	wt\id =id
	wt\tid =textid
End Function
Function SetWindowText(id,WtText$)
	For wt.WindowText=Each WindowText
		If wt\tid=id
			wt\Text$=WtText$
		EndIf 
	Next 
End Function
Function CreateWindow(Title$,x,y,WiW,WiH,Id)
	Wi.Window=New Window
	Wi\X=x
	Wi\Y=y
	Wi\Width=WiW
	Wi\Height=WiH
	Wi\Title$=Title$
	Wi\Id=Id
	
	ws.Windows=New Windows
	ws\id=Id
	Return Id
End Function
Global OffsetX,OffsetY
Global MOffsetX,MOffsetY
Function SetWindowTitle(id,Title$)
	For Wi.Window=Each Window
		If Wi\id=id
			Wi\Title$=Title$
		EndIf 
	Next 
End Function

Function RenderWindow(id)
	For Wi.Window=Each Window
		If Wi\id = id 
			
			
			xText Wi\X+Wi\Width/2-xStringWidth(Wi\Title$)/2,Wi\Y+xStringHeight(Wi\Title$)-5,Wi\Title$
			
			
			
			For wb.WindowButton=Each WindowButton
				If wi\id =wb\id
					
					If wb\X+ Wi\X<mX And  wb\X+ Wi\X+ wb\Width>mX And  wb\Y+ Wi\Y<mY And  wb\Y+ Wi\Y+ wb\Height>mY
						xColor 235,235,235
						wb\state=0
						
						
						If wb\OnlyHit=0 And Md1=1
							pressed=1
						EndIf 
						
						If msh1=1 
							pressed=1
						EndIf 
						
						
						If pressed=1
							;If  id =SelectedWindowId
							wb\state=1	
							;EndIf 
							xColor 245,245,245
						EndIf 
					Else
						xColor 225,225,225
					EndIf 
					
					xRect wb\X+ Wi\X,wb\Y+ Wi\Y,wb\Width,wb\Height,1
					
					xColor 173,173,173
					xRect wb\X+ Wi\X,wb\Y+ Wi\Y,wb\Width,wb\Height,0
					
					xColor 0,0,0
					xText 1+wb\X+ Wi\X,wb\Y+ Wi\Y+1,	wb\Text$
				EndIf 
			Next 
			
			
			
			For sl.WindowsSlider=Each WindowsSlider
				If wi\id =sl\id 
					xColor 78,78,78
					xRect Wi\x+5+sl\x,Wi\y+15+sl\y,sl\xs-10,2
					
					
				;If sl\showlabel Then Text x+sl\x+(sl\xs/2),y+sl\y,sl\label,True
					
					If Md1 = False And sl\held=True Then sl\held=False
					
					If drawbutton(sl\label$+sl\value*sl\Multipler ,Wi\x+(sl\xs*sl\value)-5+sl\x,Wi\y+10+sl\y,10,20,sl\held,1) 
					sl\held = True
				EndIf 
					
					If sl\held = True
						sl\value = sl\value + (Float(mouseSpeedX*0.5)/sl\xs)
						If sl\value < 0 Then sl\value = 0
						If sl\value > 1 Then sl\value = 1
						
					EndIf
				EndIf
			Next 
			
			For ws.WindowSelc=Each WindowSelc
				If wi\id = ws\id
					size=15
					
					xColor 225,225,225
					xRect Wi\X+ws\X ,Wi\Y+ws\Y,size,size,1
					
					If msh1=1
						If  Wi\X+ws\X<mX And   Wi\X+ws\X+size>mX 
							If Wi\Y+ws\Y<mY And  Wi\Y+ws\Y+size>mY
								ws\State=1-ws\State
							EndIf 
						EndIf 
					EndIf
					
					If ws\State=1
						xColor 125,125,125
						xRect Wi\X+ws\X+4 ,Wi\Y+ws\Y+4,size-8,size-8,1
					EndIf 
					
					xColor 173,173,173
					xRect Wi\X+ws\X ,Wi\Y+ws\Y,size,size,0
					
					
					
					
					xColor 0,0,0
					xText Wi\X+ws\X +17 ,Wi\Y+ws\Y,ws\Text$
				EndIf 
			Next 
			
			For wt.WindowText=Each WindowText
				If wi\id = wt\id
					xColor 80,80,80
					xText Wi\X+Wt\X,Wi\Y+Wt\Y,wt\Text$
				EndIf 
			Next 
		EndIf 
	Next
	
End Function


Function RenderWindow2(id)
	For Wi.Window=Each Window
		If Wi\id = id 
			
			
			
			If Mh2=1
				If  Wi\X<mX And  Wi\X+ Wi\Width>mX And  Wi\Y<mY And  Wi\Y+ Wi\Height>mY
					MOffsetX=mX
					MOffsetY=mY
					OffsetX=Wi\X
					OffsetY=Wi\Y
					Wi\MoweWindow=1
					SelectedWindowId=Wi\id
					Mh2=0
				EndIf 
			EndIf
			
			If 	Md2=1
				If  Wi\MoweWindow=1
					wi\x=mX-MOffsetX+OffsetX
					wi\y=mY-MOffsetY+OffsetY
				EndIf
			Else 
				Wi\MoweWindow=0
			EndIf 
			
			
			xColor 51,51,51
			xRect Wi\X,Wi\Y,Wi\Width,Wi\Height,1
			
			xColor 78,78,78
			xRect Wi\X,Wi\Y,Wi\Width,Wi\Height,0
			
			
			xColor 107,107,107
			xText Wi\X+Wi\Width/2-xStringWidth(Wi\Title$)/2,Wi\Y+xStringHeight(Wi\Title$)-5,Wi\Title$
			
			
			
			For wb.WindowButton=Each WindowButton
				If wi\id =wb\id
					
					If wb\X+ Wi\X<mX And  wb\X+ Wi\X+ wb\Width>mX And  wb\Y+ Wi\Y<mY And  wb\Y+ Wi\Y+ wb\Height>mY
						xColor 107,107,107
						wb\state=0
						
						
						If wb\OnlyHit=0 And Md1=1
							pressed=1
						EndIf 
						
						If msh1=1 
							pressed=1
						EndIf 
						
						
						If pressed=1
							;If  id =SelectedWindowId
							wb\state=1	
							;EndIf 
							xColor 65,141,255
						EndIf 
					Else
						xColor 64,64,64
					EndIf 
					
					xRect wb\X+ Wi\X,wb\Y+ Wi\Y,wb\Width,wb\Height,1
					
					xColor 78,78,78
					xRect wb\X+ Wi\X,wb\Y+ Wi\Y,wb\Width,wb\Height,0
					
					xColor 200,200,200
					xText 2+wb\X+ Wi\X,wb\Y+ Wi\Y+2,	wb\Text$
				EndIf 
			Next 
			
			
			
			For sl.WindowsSlider=Each WindowsSlider
				If wi\id =sl\id 
					xColor 78,78,78
					xRect Wi\x+5+sl\x,Wi\y+15+sl\y,sl\xs-10,2
					
					
				;If sl\showlabel Then Text x+sl\x+(sl\xs/2),y+sl\y,sl\label,True
					
					If Md1 = False And sl\held=True Then sl\held=False
					
					If drawbutton(sl\label$+sl\value*sl\Multipler ,Wi\x+(sl\xs*sl\value)-5+sl\x,Wi\y+10+sl\y,10,20,sl\held,1) 
						sl\held = True
					EndIf 
					
					If sl\held = True
						sl\value = sl\value + (Float(mouseSpeedX*0.5)/sl\xs)
						If sl\value < 0 Then sl\value = 0
						If sl\value > 1 Then sl\value = 1
						
					EndIf
				EndIf
			Next 
			
			For ws.WindowSelc=Each WindowSelc
				If wi\id = ws\id
					size=15
					
					xColor 64,64,64
					xRect Wi\X+ws\X ,Wi\Y+ws\Y,size,size,1
					
					If msh1=1
						If  Wi\X+ws\X<mX And   Wi\X+ws\X+size>mX 
							If Wi\Y+ws\Y<mY And  Wi\Y+ws\Y+size>mY
								ws\State=1-ws\State
							EndIf 
						EndIf 
					EndIf
					
					If ws\State=1
						xColor 200,200,200
						xRect Wi\X+ws\X+4 ,Wi\Y+ws\Y+4,size-8,size-8,1
					EndIf 
					
					xColor 78,78,78
					xRect Wi\X+ws\X ,Wi\Y+ws\Y,size,size,0
					
					
					
					
					xColor 200,200,200
					xText Wi\X+ws\X +17 ,Wi\Y+ws\Y,ws\Text$
				EndIf 
			Next 
			
			For wt.WindowText=Each WindowText
				If wi\id = wt\id
					xText Wi\X+Wt\X,Wi\Y+Wt\Y,wt\Text$
				EndIf 
			Next 
		EndIf 
	Next
	
End Function

Function drawbutton(label$,x,y,xs,ys,active,enable)
	
	If enable And xRectsOverlap(x,y,xs,ys,mX,mY,1,1) Then over = True
	
	If over = True 
		If Md1 Then active = True
	EndIf
	xColor 125,125,125
	If active
		xColor 135,135,135
	EndIf 
	
	
	xRect x,y,xs,ys,1
	xColor 0,0,0
	xText  x+15,y+5,label$
	
	If enable = True And over = True And Md1 Then Return True Else Return False
End Function


Function MouseOverlap(x1,y1,x2,y2,mmx,mmy)
	If  x1<mmx And   y1<mmy 
		If x2>mmx And  y2>mmy
			Return 1
		EndIf 
	EndIf 
End Function




Function DrawGui()
	For ws.Windows =Each Windows
		RenderWindow(ws\id) 
	Next 
End Function




Type Trigger
	Field Id 
	Field Name
	Field State
	Field mesh
End Type

Function LoadTriggers(patch$,TrigMesh)
	DebugLog patch$
	INI_OpenFile(patch$)
	TriggerCount=INI_ReadValue("triggers_info", "triggers", "0") 
	INI_CloseFile%()
	DebugLog "triggers "+TriggerCount
	TriggersCreated=0
	For i=1 To TriggerCount
		TriggersCreated=TriggersCreated+1
		INI_OpenFile(patch$)
		id			=		INI_ReadValue("trigger_"+TriggersCreated,"id", "0")
		Name$  =   	INI_ReadValue("trigger_"+TriggersCreated,"name", "0")
		INI_CloseFile%()
		AddTrigger(Name$,id,TrigMesh)
	Next
End Function

Function AddTrigger(Name$,Id,TrigMesh)
	T.Trigger= New Trigger
	T\mesh=xFindChild(TrigMesh,Name$)
	xEntityPickMode T\mesh,2
	T\Name$=Name$
	T\State=0
	T\id=Id
	DebugLog "CreatedTrigger"+T\Name$ +"TriggerID"+Id
	Return T\mesh
End Function

Function UpdateTriggerHand(campic)
	handshowstate=0
	For T.Trigger= Each Trigger
		If T\mesh = campic
			If xEntityDistance(camera,T\mesh)<20
				handshowstate=handshowstate+1
				If xkht18=1
					T\State=1-T\State
					xEmitSound(ButtonSound,T\mesh)
				EndIf 
			EndIf
		EndIf 
	Next
	
	If handshowstate>0
		showhand=1
	Else
		showhand=0
	EndIf 
	
End Function

Function DeleteTriggers()
	;For lto.LightTriggerObj= Each LightTriggerObj
	;	Delete lto
	;Next	
	;For Lc.LightControl= Each LightControl
	;	Delete lc 
	;Next
End Function





;~IDEal Editor Parameters:
;~F#140#18E#265#354#49F
;~C#Blitz3D